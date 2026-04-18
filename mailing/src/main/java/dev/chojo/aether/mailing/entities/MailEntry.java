/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.mailing.entities;

import java.time.Instant;

/**
 * Represents a registered mail address for a user.
 */
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

    /**
     * Get the user id tied to this mail entry.
     *
     * @return the user id
     */
    public long userId() {
        return userId;
    }

    /**
     * Get the source of this mail entry.
     *
     * @return the source
     */
    public MailSource source() {
        return source;
    }

    /**
     * Get the hash of the mail address.
     *
     * @return the hash
     */
    public String hash() {
        return hash;
    }

    /**
     * Get the shortened mail address (e.g. j.***@g***.com).
     *
     * @return the shortened mail address
     */
    public String mailShort() {
        return mailShort;
    }

    /**
     * Check if this mail address is verified.
     *
     * @return true if verified
     */
    public boolean verified() {
        return verified;
    }

    /**
     * Get the time when verification was requested.
     *
     * @return the verification requested time
     */
    public Instant verificationRequested() {
        return verificationRequested;
    }

    /**
     * Get the current verification code.
     *
     * @return the verification code
     */
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
