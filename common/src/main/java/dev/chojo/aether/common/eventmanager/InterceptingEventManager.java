/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.common.eventmanager;

import dev.goldmensch.propane.event.Listener;
import io.github.kaktushose.jdac.JDACommands;
import io.github.kaktushose.jdac.property.JDACIntrospection;
import io.github.kaktushose.jdac.property.JDACScope;
import io.github.kaktushose.jdac.property.events.InteractionFinishedEvent;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.hooks.InterfacedEventManager;
import org.jspecify.annotations.NonNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InterceptingEventManager
        implements IEventManager, Listener<InteractionFinishedEvent, JDACScope, JDACIntrospection> {
    private final InterfacedEventManager eventManager = new InterfacedEventManager();
    private final Map<Class<? extends Throwable>, ExceptionHandler> exceptionHandlers = new LinkedHashMap<>();

    public InterceptingEventManager(JDACommands commands) {
        commands.introspection().subscribe(this);
    }

    @Override
    public void register(@NonNull Object listener) {
        eventManager.register(listener);
    }

    @Override
    public void unregister(@NonNull Object listener) {
        eventManager.unregister(listener);
    }

    @Override
    public @NonNull List<Object> getRegisteredListeners() {
        return eventManager.getRegisteredListeners();
    }

    @Override
    public void handle(@NonNull GenericEvent event) {
        for (Object obj : getRegisteredListeners()) {
            if (!(obj instanceof EventListener listener)) continue;
            try {
                listener.onEvent(event);
            } catch (Throwable throwable) {
                handle(throwable, new ExceptionContext(event));
            }
        }
    }

    /**
     * Register an exception handler.
     * Exception handlers are called in the order they were registered.
     *
     * @param err     error class to handle
     * @param handler handler to call when the error is thrown
     * @param <V>     exception type to handle
     */
    public <V extends Throwable> void exception(Class<V> err, ExceptionHandler handler) {
        exceptionHandlers.put(err, handler);
    }

    private void handle(Throwable err, ExceptionContext context) {
        exceptionHandlers.entrySet().stream()
                .filter(entry -> entry.getKey().isInstance(err))
                .findFirst()
                .ifPresent(entry -> entry.getValue().handle(err, context));
    }

    @Override
    public void accept(InteractionFinishedEvent event, JDACIntrospection introspection) {
        if (event.exception() == null) return;
        GenericInteractionCreateEvent interaction = event.invocationContext().event();
        handle(event.exception(), new ExceptionContext(interaction));
    }

    @Override
    public Class<InteractionFinishedEvent> event() {
        return InteractionFinishedEvent.class;
    }
}
