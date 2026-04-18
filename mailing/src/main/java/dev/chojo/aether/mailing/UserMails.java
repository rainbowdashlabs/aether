/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.mailing;

import dev.chojo.aether.mailing.entities.MailEntry;

import java.util.Map;
import java.util.Optional;

public interface UserMails {
    /// Retrieve a [MailEntry] for the given hash
    ///
    /// @param hash The hash of the mail entry to retrieve
    /// @return An optional containing the mail entry if found, empty otherwise
    Optional<MailEntry> getMail(String hash);

    /// Add a new [MailEntry] to the users mails
    ///
    /// @param mailEntry The mail entry to add
    void addMail(MailEntry mailEntry);

    /// Remove a [MailEntry] from the users mails that matches the given hash
    ///
    /// @param mailHash The hash of the [MailEntry] to remove
    /// @return True if the mail was removed, false otherwise
    boolean removeMail(String mailHash);

    /// Retrieve all registered mails for that user
    ///
    /// @return An unmodifiable map of mail hashes to mail entries
    Map<String, MailEntry> mails();

    long userId();
}
