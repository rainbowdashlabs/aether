/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.mailing.service;

import dev.chojo.aether.mailing.UserMails;
import net.dv8tion.jda.api.entities.User;

import java.util.Optional;

public interface UserMailsProvider {
    /// Get the [UserMails] for the given user id
    ///
    /// @param userId the id of the user to get the mails for
    /// @return the user mails for the given user id
    UserMails byUser(long userId);

    /// Get the [UserMails] for the given user id
    ///
    /// @param user the user to get the mails for
    /// @return the user mails for the given user id
    default UserMails byUser(User user) {
        return byUser(user.getIdLong());
    }

    /// Get the [UserMails] by a hash associated with the user.
    ///
    /// @param mailHash the hash of the mail to get
    /// @return the user mails for the given hash if it exists
    Optional<UserMails> byHash(String mailHash);
}
