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
import java.util.UUID;

/**
 * Main mailing configuration class containing SMTP, IMAP settings and user credentials.
 */
public class Mailing {
    private String mailSalt = Hashing.sha256()
            .hashString(UUID.randomUUID().toString(), StandardCharsets.UTF_8)
            .toString();
    private MailSettings smtp = new MailSettings();
    private MailSettings imap = new MailSettings();
    private String user = "";
    private String password = "";
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
     */
    public String mailHash(String mail) {
        return Hashing.sha256()
                .hashString(mail + mailSalt, StandardCharsets.UTF_8)
                .toString();
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
