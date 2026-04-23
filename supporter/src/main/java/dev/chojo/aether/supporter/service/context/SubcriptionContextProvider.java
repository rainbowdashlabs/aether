/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.supporter.service.context;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

/**
 * Provider for {@link ISubscriptionContext}s.
 * This interface should be implemented to provide subscription information for users and guilds.
 */
public interface SubcriptionContextProvider {
    /**
     * Returns the merged subscription context for a user and a guild.
     *
     * @param user  The user.
     * @param guild The guild.
     * @return The merged subscription context.
     */
    default ISubscriptionContext getSubscriptionContext(User user, Guild guild) {
        ISubscriptionContext userContext = getSubscriptionContext(user);
        ISubscriptionContext guildContext = getSubscriptionContext(guild);
        return guildContext.merge(userContext);
    }

    /**
     * Returns the subscription context for a user.
     *
     * @param user The user.
     * @return The subscription context.
     */
    ISubscriptionContext getSubscriptionContext(User user);

    /**
     * Returns the subscription context for a guild.
     *
     * @param guild The guild.
     * @return The subscription context.
     */
    ISubscriptionContext getSubscriptionContext(Guild guild);
}
