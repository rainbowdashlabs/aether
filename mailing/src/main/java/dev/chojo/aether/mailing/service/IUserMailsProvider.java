/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.mailing.service;

import dev.chojo.aether.mailing.IUserMails;
import dev.chojo.aether.mailing.entities.AMailEntry;
import net.dv8tion.jda.api.entities.User;

import java.util.Optional;

public interface IUserMailsProvider<V extends AMailEntry> {
    /**
     * Get the {@link IUserMails} for the given user id
     *
     * @param userId the id of the user to get the mails for
     * @return the user mails for the given user id
     */
    IUserMails<V> byUser(long userId);

    /**
     * Get the {@link IUserMails} for the given user id
     *
     * @param user the user to get the mails for
     * @return the user mails for the given user id
     */
    default IUserMails<V> byUser(User user) {
        return byUser(user.getIdLong());
    }

    /**
     * Get the {@link IUserMails} by a hash associated with the user.
     *
     * @param mailHash the hash of the mail to get
     * @return the user mails for the given hash if it exists
     */
    Optional<IUserMails<V>> byHash(String mailHash);
}
