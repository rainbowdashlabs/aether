/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.supporter.service;

import dev.chojo.aether.supporter.configuration.SupporterConfiguration;
import dev.chojo.aether.supporter.configuration.modules.Interactions;
import dev.chojo.aether.supporter.configuration.modules.subscriptions.platform.purchase.Purchase;
import dev.chojo.aether.supporter.service.context.SubcriptionContextProvider;
import dev.chojo.aether.supporter.service.context.SubscriptionContext;
import dev.chojo.aether.supporter.service.context.SupporterErrorSupplier;
import dev.chojo.aether.supporter.service.context.SupporterValidator;
import io.github.kaktushose.jdac.dispatching.context.InvocationContext;
import io.github.kaktushose.jdac.dispatching.middleware.Middleware;
import net.dv8tion.jda.api.entities.Entitlement;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.Collections;
import java.util.stream.Collectors;

public class SupporterMiddleware<
                SID extends Enum<?>, FID extends Enum<?>, PF extends Enum<?>, PT extends Enum<PT> & Purchase, PR, FM>
        implements Middleware {
    private final SubcriptionContextProvider contextProvider;
    private final SupporterConfiguration<SID, FID, PF, PT, PR, FM> configuration;
    private final SupporterErrorSupplier errorSupplier;

    public SupporterMiddleware(
            SubcriptionContextProvider contextProvider,
            SupporterConfiguration<SID, FID, PF, PT, PR, FM> configuration,
            SupporterErrorSupplier errorSupplier) {
        this.contextProvider = contextProvider;
        this.configuration = configuration;
        this.errorSupplier = errorSupplier;
    }

    public SubscriptionContext buildContext(GenericInteractionCreateEvent event) {
        SubscriptionContext context = convert(event);
        if (event.isFromGuild()) {
            return context.merge(contextProvider.getSubscriptionContext(event.getUser(), event.getGuild()));
        }
        return context.merge(contextProvider.getSubscriptionContext(event.getUser()));
    }

    @Override
    public void accept(InvocationContext<?> context) {
        SubscriptionContext subCtx = buildContext(context.event());
        Interactions interactions = configuration.interactions();
        boolean hasAccess =
                switch (context.event()) {
                    case UserContextInteractionEvent event -> interactions.hasAccess(event, subCtx);
                    case MessageContextInteractionEvent event -> interactions.hasAccess(event, subCtx);
                    case SlashCommandInteractionEvent event -> interactions.hasAccess(event, subCtx);
                    case CommandAutoCompleteInteractionEvent event -> interactions.hasAccess(event, subCtx);
                    default -> true;
                };

        if (!hasAccess) {
            if (context.event() instanceof CommandAutoCompleteInteractionEvent event) {
                event.replyChoices(Collections.emptyList()).complete();
                Thread.currentThread().interrupt();
            } else {
                context.cancel(
                        errorSupplier.getCommandError(),
                        MessageCreateData.fromContent("You do not have access to this command."));
            }
            return;
        }

        context.keyValueStore()
                .put(SupporterKeys.SUBSCRIPTION_CONTEXT, subCtx)
                .put(SupporterKeys.SUBSCRIPTION_VALIDATOR, new SupporterValidator<>(subCtx, configuration));
    }

    private SubscriptionContext convert(GenericInteractionCreateEvent event) {
        return () ->
                event.getEntitlements().stream().map(Entitlement::getSkuIdLong).collect(Collectors.toSet());
    }
}
