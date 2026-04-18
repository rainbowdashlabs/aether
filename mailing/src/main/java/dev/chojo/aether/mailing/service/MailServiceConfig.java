/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.mailing.service;

import dev.chojo.aether.mailing.configuration.Mailing;

public record MailServiceConfig(
        String host, Mailing mailing, MailTemplates templates, UserMailsProvider userMailsProvider, Runnable cleanup) {}
