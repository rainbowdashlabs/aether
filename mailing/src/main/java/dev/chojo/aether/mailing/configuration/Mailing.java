/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.mailing.configuration;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

/**
 * Main mailing configuration class containing SMTP, IMAP settings and user credentials.
 */
public class Mailing {
    private String mailSalt = "";
    private MailSettings smtp = new MailSettings();
    private MailSettings imap = new MailSettings();
    private String user = "";
    private String password = "";
    private String mailer = "";
    private String confirmationPath = "/user/settings";
    private Map<String, String> properties = Collections.emptyMap();

    /**
     * Get the mail salt used for hashing.
     *
     * @return the mail salt
     */
    public String mailSalt() {
        return mailSalt;
    }

    /**
     * Calculate a hash for the given email address using the mail salt.
     *
     * @param mail the mail address to hash
     * @return the hashed mail address
     * @throws IllegalStateException if no mail salt is configured
     */
    public String mailHash(String mail) {
        requireMailSalt();
        return Hashing.sha256()
                .hashString(mail + mailSalt, StandardCharsets.UTF_8)
                .toString();
    }

    /**
     * Checks that a mail salt is configured. Without a fixed salt, stored mail hashes stop matching after a restart.
     *
     * @throws IllegalStateException if the mail salt is blank
     */
    public void requireMailSalt() {
        if (mailSalt == null || mailSalt.isBlank()) {
            throw new IllegalStateException("No mail salt configured. Set mailSalt in the mailing configuration.");
        }
    }

    /**
     * Get the SMTP settings.
     *
     * @return the SMTP settings
     */
    public MailSettings smtp() {
        return smtp;
    }

    /**
     * Get the IMAP settings.
     *
     * @return the IMAP settings
     */
    public MailSettings imap() {
        return imap;
    }

    /**
     * Get the username for authentication.
     *
     * @return the username
     */
    public String user() {
        return user;
    }

    /**
     * Get the password for authentication.
     *
     * @return the password
     */
    public String password() {
        return password;
    }

    /**
     * Get the value of the X-Mailer header. Blank means the header is not set.
     *
     * @return the mailer name
     */
    public String mailer() {
        return mailer;
    }

    /**
     * Get the path of the page that confirms a mail address, relative to the host of the mail service. The confirmation
     * link adds the query parameters {@code code} and {@code hash}.
     *
     * @return the confirmation path
     */
    public String confirmationPath() {
        return confirmationPath;
    }

    /**
     * Get all mail properties including SMTP and IMAP settings.
     *
     * @return the properties
     */
    public Properties properties() {
        Properties props = new Properties();
        props.putAll(smtp().properties("smtp"));
        props.putAll(imap().properties("imap"));
        props.putAll(properties);
        return props;
    }
}
