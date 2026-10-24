/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.mailing.service;

import dev.chojo.aether.mailing.IUserMails;
import dev.chojo.aether.mailing.configuration.Mailing;
import dev.chojo.aether.mailing.entities.AMailEntry;
import dev.chojo.aether.mailing.entities.FailureReason;
import dev.chojo.aether.mailing.entities.Mail;
import dev.chojo.aether.mailing.entities.MailSource;
import dev.chojo.aether.mailing.entities.Result;
import dev.chojo.aether.mailing.util.Retry;
import jakarta.activation.DataHandler;
import jakarta.mail.Address;
import jakarta.mail.Authenticator;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.validator.routines.EmailValidator;
import org.eclipse.angus.mail.imap.IMAPStore;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Core mail service for handling mail registration, verification, and sending.
 */
public abstract class AMailService<V extends AMailEntry> {
    private static final Pattern MAIL_SHORTER = Pattern.compile("(.{2}).+?@.+?(.{2}\\..+)");
    private static final Logger log = getLogger(AMailService.class);
    private final MailServiceConfig<V> config;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    /**
     * Create a new mail service with the given configuration. Call {@link #start()} once to run the cleanup task.
     *
     * @param config the configuration
     * @throws IllegalStateException if the mailing configuration has no mail salt
     */
    public AMailService(MailServiceConfig<V> config) {
        config.mailing().requireMailSalt();
        this.config = config;
    }

    /**
     * Schedules the cleanup task of the configuration 30 minutes from now and every 60 minutes after that. Call it once.
     */
    public void start() {
        executor.scheduleAtFixedRate(this::cleanupExpiredMails, 30, 60, TimeUnit.MINUTES);
    }

    /**
     * Register a new mail address for a user and send a verification email.
     *
     * @param user   the user id
     * @param mail   the mail address
     * @param source the source of the mail registration
     * @return the result of the registration
     */
    public Result<V, FailureReason> registerAndPromptVerify(long user, String mail, MailSource source) {
        String hash = mailHash(mail);
        Optional<IUserMails<V>> entry = mailProvider().byHash(hash);
        V mailEntry;
        if (entry.isPresent()) {
            mailEntry = entry.get().getMail(hash).get();
            if (mailEntry.verified()) {
                return Result.failure(FailureReason.ALREADY_REGISTERED);
            }
            mailEntry.updateUser(user);
        } else {
            Result<V, FailureReason> newEntry = createMailEntry(user, mail, source);
            if (newEntry.isFailure()) return newEntry;
            mailEntry = newEntry.result();
            mailProvider().byUser(user).addMail(mailEntry);
        }

        String url = confirmationUrl(hash, mailEntry.verificationCode());
        executor.execute(() -> sendMail(templates().accountConfirmation(mail, url)));

        return Result.success(mailEntry);
    }

    private String confirmationUrl(String mailHash, String code) {
        return "%s%s?code=%s&hash=%s"
                .formatted(
                        host(),
                        mailing().confirmationPath(),
                        URLEncoder.encode(code, StandardCharsets.UTF_8),
                        URLEncoder.encode(mailHash, StandardCharsets.UTF_8));
    }

    /**
     * Attempts to verify the mail entry with the given verification code.
     *
     * @param user             user that owns that mail entry
     * @param mailHash         hash of the mail entry
     * @param verificationCode verification code to verify with
     * @return a failure reason if the verification failed, otherwise null
     */
    @Nullable
    public FailureReason verifyMail(long user, String mailHash, String verificationCode) {
        var mails = mailProvider().byHash(mailHash);
        if (mails.isEmpty()) return FailureReason.UNKNOWN_ADDRESS;
        if (mails.get().userId() != user) return FailureReason.WRONG_USER;
        AMailEntry mail = mails.get().getMail(mailHash).get();
        if (!mail.verificationCode().equals(verificationCode)) return FailureReason.INVALID_CODE;
        if (mail.verificationRequested().isBefore(Instant.now().minus(1, ChronoUnit.HOURS)))
            return FailureReason.CODE_EXPIRED;
        mail.verify();
        return null;
    }

    /**
     * Register a verified mail address for a user.
     *
     * @param user   the user id
     * @param mail   the mail address
     * @param source the source of the mail registration
     * @return the result of the registration
     */
    public Result<V, FailureReason> registerVerifiedMail(long user, String mail, MailSource source) {
        var mails = mailProvider().byHash(mailHash(mail));
        if (mails.isPresent()) {
            // This is still considered a success because the mail address is already registered and present in the
            // result.
            V mailEntry = mails.get().getMail(mailHash(mail)).get();
            mailEntry.updateUser(user);
            // If the mail entry is not verified, verify it.
            if (!mailEntry.verified()) {
                mailEntry.verify();
            }
            return new Result<>(mailEntry, FailureReason.ALREADY_REGISTERED, true);
        }

        Result<V, FailureReason> result = createMailEntry(user, mail, source);
        if (!result.isSuccess()) return result;
        V mailEntry = result.result();
        IUserMails<V> userMails = mailProvider().byUser(user);
        userMails.addMail(mailEntry);
        mailEntry.verify();
        return Result.success(mailEntry);
    }

    /**
     * Create a new {@link AMailEntry} for a user.
     *
     * @param user   the user id
     * @param mail   the mail address
     * @param source the source of the mail registration
     * @return the result containing the mail entry if successful
     */
    public Result<V, FailureReason> createMailEntry(long user, String mail, MailSource source) {
        boolean valid = EmailValidator.getInstance().isValid(mail);
        if (!valid) {
            return Result.failure(FailureReason.INVALID_FORMAT);
        }

        Matcher matcher = MAIL_SHORTER.matcher(mail);

        if (!matcher.matches()) {
            return Result.failure(FailureReason.INVALID_FORMAT);
        }

        var mailShort = "%s***@***%s".formatted(matcher.group(1), matcher.group(2));

        return Result.success(createMailEntry(
                user,
                source,
                mailHash(mail),
                mailShort,
                false,
                Instant.now(),
                UUID.randomUUID().toString()));
    }

    protected abstract V createMailEntry(
            long user,
            MailSource source,
            String mailHash,
            String mailShort,
            boolean verified,
            Instant verificationRequested,
            String verificationCode);

    /**
     * Get the time in seconds a user must wait before they can request another verification mail.
     *
     * @param user the user id
     * @return the retry after time in seconds
     */
    public long getRetryAfterSeconds(long user) {
        IUserMails<V> userMails = mailProvider().byUser(user);
        return userMails.mails().values().stream()
                .filter(m -> MailSource.USER.equals(m.source()))
                .map(m -> m.verificationRequested().plus(5, ChronoUnit.MINUTES).getEpochSecond()
                        - Instant.now().getEpochSecond())
                .max(Long::compareTo)
                .orElse(0L);
    }

    /**
     * Calculate the hash for the given mail address.
     *
     * @param mail the mail address
     * @return the hashed mail address
     */
    public String mailHash(String mail) {
        return mailing().mailHash(mail);
    }

    /**
     * Send an email over SMTP and store a copy in the folder {@code Sent} below the IMAP inbox.
     * <p>
     * Sending and storing are tried three times each. A mail that could not be sent is not stored, and a mail that
     * could not be stored is not sent again.
     *
     * @param mail the email to send
     */
    public void sendMail(Mail mail) {
        Session session = createSession();
        MimeMessage mimeMessage;
        try {
            mimeMessage = buildMessage(session, mail);
        } catch (MessagingException e) {
            log.error("Could not build mail", e);
            return;
        }

        boolean sent = Retry.retryAndReturn(
                        3, () -> sendMessage(mimeMessage), err -> log.error("Could not send mail", err))
                .isPresent();
        if (!sent) {
            log.error("Could not send mail after three tries.");
            return;
        }

        try (IMAPStore imapStore = createImapStore(session)) {
            boolean stored = Retry.retryAndReturn(
                            3,
                            () -> storeMessage(imapStore, mimeMessage),
                            err -> log.error("Could not store mail", err))
                    .isPresent();
            if (stored) {
                log.debug("Mail stored");
            } else {
                log.error("Could not store mail after three tries.");
            }
        } catch (MessagingException e) {
            log.error("Could not connect to the IMAP store", e);
        }
    }

    private Session createSession() {
        log.debug("Creating new mail session");
        return Session.getInstance(mailing().properties(), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailing().user(), mailing().password());
            }
        });
    }

    private IMAPStore createImapStore(Session session) throws MessagingException {
        log.debug("Creating imap store");
        IMAPStore imapStore = (IMAPStore) session.getStore("imap");
        imapStore.connect();
        return imapStore;
    }

    private MimeMessage buildMessage(Session session, Mail mail) throws MessagingException {
        var message = new MimeMessage(session);
        message.addFrom(new Address[] {new InternetAddress(mailing().user())});
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(mail.address(), false));
        message.setDataHandler(new DataHandler(mail.text(), "text/html; charset=UTF-8"));
        message.setSubject(mail.subject());
        if (!mailing().mailer().isBlank()) {
            message.setHeader("X-Mailer", mailing().mailer());
        }
        message.setSentDate(new Date());
        return message;
    }

    private boolean storeMessage(IMAPStore store, MimeMessage message) throws MessagingException {
        Folder sent = store.getFolder("inbox").getFolder("Sent");
        if (!sent.exists()) {
            sent.create(Folder.HOLDS_MESSAGES);
        }
        sent.appendMessages(new Message[] {message});
        return true;
    }

    private boolean sendMessage(MimeMessage message) throws MessagingException {
        log.info("Sending mail to {}", ((InternetAddress) message.getAllRecipients()[0]).getAddress());
        Transport.send(message, mailing().user(), mailing().password());
        log.info("Mail sent.");
        return true;
    }

    /**
     * Get the configured public host for URLs.
     *
     * @return the host
     */
    public String host() {
        return config.host();
    }

    /**
     * Get the mailing configuration.
     *
     * @return the mailing configuration
     */
    public Mailing mailing() {
        return config.mailing();
    }

    /**
     * Get the mail templates.
     *
     * @return the mail templates
     */
    public MailTemplates templates() {
        return config.templates();
    }

    /**
     * Get the user mails provider.
     *
     * @return the user mails provider
     */
    public IUserMailsProvider<V> mailProvider() {
        return config.userMailsProvider();
    }

    /**
     * Clean up expired mail entries.
     */
    private void cleanupExpiredMails() {
        config.cleanup().run();
    }
}
