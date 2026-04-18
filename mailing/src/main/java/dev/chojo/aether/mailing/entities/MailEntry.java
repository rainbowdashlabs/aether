/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.mailing.entities;

import java.time.Instant;

public abstract class MailEntry {
    private final MailSource source;
    private final String hash;
    private final String mailShort;
    protected long userId;
    protected boolean verified;
    protected Instant verificationRequested;
    protected String verificationCode;

    public MailEntry(
            long userId,
            MailSource source,
            String hash,
            String mailShort,
            boolean verified,
            Instant verificationRequested,
            String verificationCode) {
        this.userId = userId;
        this.source = source;
        this.hash = hash;
        this.mailShort = mailShort;
        this.verified = verified;
        this.verificationRequested = verificationRequested;
        this.verificationCode = verificationCode;
    }

    /**
     * Mark this mail as verified.
     */
    public abstract void verify();

    /**
     * Regenerates the verification code.
     * This also sets {@link #verificationRequested} to {@link java.time.Instant#now()} and marks the mail as unverified.
     */
    public abstract void regenerateVerificationCode();

    public long userId() {
        return userId;
    }

    public MailSource source() {
        return source;
    }

    public String hash() {
        return hash;
    }

    public String mailShort() {
        return mailShort;
    }

    public boolean verified() {
        return verified;
    }

    public Instant verificationRequested() {
        return verificationRequested;
    }

    public String verificationCode() {
        return verificationCode;
    }

    /**
     * Update the user tied to this mail entry.
     * Regenerates the verification code.
     * This also regenerated the verification code.
     *
     * @param user user to update
     */
    public abstract void updateUser(long user);
}
