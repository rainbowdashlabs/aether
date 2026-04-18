/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.supporter.service.context;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public interface SubcriptionContextProvider {
    default SubscriptionContext getSubscriptionContext(User user, Guild guild) {
        SubscriptionContext userContext = getSubscriptionContext(user);
        SubscriptionContext guildContext = getSubscriptionContext(guild);
        return guildContext.merge(userContext);
    }

    SubscriptionContext getSubscriptionContext(User user);

    SubscriptionContext getSubscriptionContext(Guild guild);
}
