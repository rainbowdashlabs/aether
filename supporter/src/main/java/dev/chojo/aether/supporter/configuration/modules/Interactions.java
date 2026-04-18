/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.supporter.configuration.modules;

import dev.chojo.aether.supporter.service.context.AccessCheckResult;
import dev.chojo.aether.supporter.service.context.SubscriptionContext;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptySet;

@SuppressWarnings({"FieldMayBeFinal", "MismatchedQueryAndUpdateOfCollection"})
public class Interactions {
    private Map<String, Set<Long>> slash = new HashMap<>();
    private Map<String, Set<Long>> messages = new HashMap<>();
    private Map<String, Set<Long>> users = new HashMap<>();

    public Interactions() {}

    public AccessCheckResult hasAccess(
            SlashCommandInteractionEvent interaction, SubscriptionContext subscriptionContext) {
        return hasAccess(slash(interaction.getFullCommandName()), subscriptionContext);
    }

    public AccessCheckResult hasAccess(
            CommandAutoCompleteInteractionEvent interaction, SubscriptionContext subscriptionContext) {
        return hasAccess(slash(interaction.getFullCommandName()), subscriptionContext);
    }

    public AccessCheckResult hasAccess(
            MessageContextInteractionEvent interaction, SubscriptionContext subscriptionContext) {
        return hasAccess(message(interaction.getFullCommandName()), subscriptionContext);
    }

    public AccessCheckResult hasAccess(
            UserContextInteractionEvent interaction, SubscriptionContext subscriptionContext) {
        return hasAccess(user(interaction.getFullCommandName()), subscriptionContext);
    }

    public Set<Long> slash(String name) {
        return slash.getOrDefault(name, emptySet());
    }

    public Set<Long> message(String name) {
        return messages.getOrDefault(name, emptySet());
    }

    public Set<Long> user(String name) {
        return users.getOrDefault(name, emptySet());
    }

    private AccessCheckResult hasAccess(Set<Long> enabledBy, SubscriptionContext subscriptionContext) {
        if (!enabledBy.isEmpty()) {
            return subscriptionContext.hasAccess(enabledBy);
        }
        return AccessCheckResult.success();
    }
}
