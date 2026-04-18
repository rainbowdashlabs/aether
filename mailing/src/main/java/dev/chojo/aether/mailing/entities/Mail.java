/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.mailing.entities;

/**
 * Represents an email to be sent.
 *
 * @param address the recipient address
 * @param subject the email subject
 * @param text    the email body (HTML)
 */
public record Mail(String address, String subject, String text) {}
