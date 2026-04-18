/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.mailing.configuration;

import java.util.Properties;

public class MailSettings {
    private String host = "";
    private int port = 665;
    private boolean ssl = false;

    public String host() {
        return host;
    }

    public int port() {
        return port;
    }

    public boolean ssl() {
        return ssl;
    }

    public Properties properties(String prefix) {
        Properties props = new Properties();
        props.put("mail.%s.host".formatted(prefix), host());
        props.put("mail.%s.port".formatted(prefix), port());
        props.put("mail.%s.ssl.enable".formatted(prefix), ssl());
        return props;
    }
}
