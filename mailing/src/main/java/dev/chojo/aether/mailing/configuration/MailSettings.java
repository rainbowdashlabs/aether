/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.mailing.configuration;

import java.util.Properties;

/**
 * Settings for a mail service (SMTP or IMAP).
 */
public class MailSettings {
    private String host = "";
    private int port = 665;
    private boolean ssl = false;

    /**
     * Get the host of the mail service.
     *
     * @return the host
     */
    public String host() {
        return host;
    }

    /**
     * Get the port of the mail service.
     *
     * @return the port
     */
    public int port() {
        return port;
    }

    /**
     * Check if SSL is enabled for the mail service.
     *
     * @return true if SSL is enabled
     */
    public boolean ssl() {
        return ssl;
    }

    /**
     * Create {@link Properties} for the mail service.
     *
     * @param prefix the prefix to use for the properties (e.g. "smtp" or "imap")
     * @return the properties
     */
    public Properties properties(String prefix) {
        Properties props = new Properties();
        props.put("mail.%s.host".formatted(prefix), host());
        props.put("mail.%s.port".formatted(prefix), port());
        props.put("mail.%s.ssl.enable".formatted(prefix), ssl());
        return props;
    }
}
