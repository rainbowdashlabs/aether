/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.mailing.service;

import dev.chojo.aether.mailing.entities.Mail;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class MailTemplates {
    private final String mailRoot;
    private final ClassLoader classLoader;

    public MailTemplates() {
        mailRoot = "mails/";
        classLoader = getClass().getClassLoader();
    }

    public MailTemplates(String mailRoot, ClassLoader classLoader) {
        this.mailRoot = mailRoot;
        this.classLoader = classLoader;
    }

    public Mail kofiUserNotFound(String address, String host) {
        String subject = "Thank you for your purchase! - Link your Account to your Ko-fi Account";
        String html =
                loadTemplate("link_account.html").replace("{{ mail }}", address).replace("{{ host }}", host);
        return new Mail(address, subject, html);
    }

    public Mail accountConfirmation(String address, String host, String mailHash, String code) {
        String subject = "Confirm your email address";
        String url = "{{ host }}/user/settings?code={{ code }}&hash={{ mailhash }}"
                .replace("{{ host }}", host)
                .replace("{{ mailhash }}", mailHash)
                .replace("{{ code }}", code);

        String html = loadTemplate("account_confirmation.html")
                .replace("{{ url }}", url)
                .replace("{{ mail }}", address);
        return new Mail(address, subject, html);
    }

    public String loadTemplate(String name) {
        try (InputStream is = Mail.class.getClassLoader().getResourceAsStream(mailRoot + name)) {
            if (is == null) {
                throw new RuntimeException("Template not found: " + name);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Could not load mail template", e);
        }
    }
}
