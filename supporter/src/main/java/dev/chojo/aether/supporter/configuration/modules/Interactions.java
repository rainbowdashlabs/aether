/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.supporter.configuration.modules;

import dev.chojo.aether.supporter.configuration.modules.dummy.InteractionFeature;
import dev.chojo.aether.supporter.service.context.AccessCheckResult;
import dev.chojo.aether.supporter.service.context.ISubscriptionContext;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptySet;

/**
 * Holds the interaction based access rules.
 * This class manages access rules for slash commands, message context interactions and user context interactions.
 */
@SuppressWarnings({"FieldMayBeFinal", "MismatchedQueryAndUpdateOfCollection"})
public class Interactions {
    private Map<String, Set<Long>> slash = new HashMap<>();
    private Map<String, Set<Long>> messages = new HashMap<>();
    private Map<String, Set<Long>> users = new HashMap<>();

    public Interactions() {}

    /**
     * Checks if the given interaction has access based on the configured rules and the user's subscription context.
     *
     * @param interaction         The interaction event.
     * @param subscriptionContext The user's subscription context.
     * @return The result of the access check.
     */
    public AccessCheckResult hasAccess(
            SlashCommandInteractionEvent interaction, ISubscriptionContext subscriptionContext) {
        return hasAccess(get(interaction), subscriptionContext);
    }

    /**
     * Checks if the given interaction has access based on the configured rules and the user's subscription context.
     *
     * @param interaction         The interaction event.
     * @param subscriptionContext The user's subscription context.
     * @return The result of the access check.
     */
    public AccessCheckResult hasAccess(
            CommandAutoCompleteInteractionEvent interaction, ISubscriptionContext subscriptionContext) {
        return hasAccess(get(interaction), subscriptionContext);
    }

    /**
     * Checks if the given interaction has access based on the configured rules and the user's subscription context.
     *
     * @param interaction         The interaction event.
     * @param subscriptionContext The user's subscription context.
     * @return The result of the access check.
     */
    public AccessCheckResult hasAccess(
            MessageContextInteractionEvent interaction, ISubscriptionContext subscriptionContext) {
        return hasAccess(get(interaction), subscriptionContext);
    }

    /**
     * Checks if the given interaction has access based on the configured rules and the user's subscription context.
     *
     * @param interaction         The interaction event.
     * @param subscriptionContext The user's subscription context.
     * @return The result of the access check.
     */
    public AccessCheckResult hasAccess(
            UserContextInteractionEvent interaction, ISubscriptionContext subscriptionContext) {
        return hasAccess(get(interaction), subscriptionContext);
    }

    private Map<String, Set<Long>> getMap(CommandInteractionPayload event) {
        return switch (event.getCommandType()) {
            case SLASH -> slash;
            case MESSAGE -> messages;
            case USER -> users;
            default -> throw new IllegalArgumentException("Unsupported interaction type: " + event.getType());
        };
    }

    private InteractionFeature get(CommandInteractionPayload event) {
        Map<String, Set<Long>> map = getMap(event);
        String name = event.getFullCommandName();
        return new InteractionFeature(name, map.getOrDefault(name, emptySet()), event.getCommandType());
    }

    private AccessCheckResult hasAccess(InteractionFeature enabledBy, ISubscriptionContext subscriptionContext) {
        return enabledBy.test(subscriptionContext);
    }
}
