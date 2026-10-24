/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.mailing.service;

import dev.chojo.aether.mailing.entities.Mail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MailTemplatesTest {
    @Test
    void loadsTemplatesFromTheGivenClassLoader(@TempDir Path root) throws IOException {
        Files.createDirectories(root.resolve("templates"));
        Files.writeString(root.resolve("templates/account_confirmation.html"), "{{ mail }} {{ url }}");
        try (URLClassLoader loader = new URLClassLoader(new URL[] {root.toUri().toURL()}, null)) {
            Mail mail = new MailTemplates("templates/", loader).accountConfirmation("a@b.c", "https://x/confirm");
            assertEquals("a@b.c https://x/confirm", mail.text());
        }
    }
}
