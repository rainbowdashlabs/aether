/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.mailing.service;

import dev.chojo.aether.mailing.configuration.Mailing;

/**
 * Configuration for the {@link AMailService}.
 *
 * @param host              the configured public host for URLs
 * @param mailing           the mailing configuration (SMTP, IMAP, credentials)
 * @param templates         the mail templates
 * @param userMailsProvider the provider for user mails
 * @param cleanup           a runnable to clean up expired mails
 */
public record MailServiceConfig(
        String host, Mailing mailing, MailTemplates templates, UserMailsProvider userMailsProvider, Runnable cleanup) {
    public static final MailServiceConfig DEFAULT = new MailServiceConfig("localhost",
}
