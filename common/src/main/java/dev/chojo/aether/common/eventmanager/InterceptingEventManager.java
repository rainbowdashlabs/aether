/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.common.eventmanager;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.hooks.InterfacedEventManager;

import java.util.List;
import java.util.function.BiConsumer;

public class InterceptingEventManager implements IEventManager {
    private final InterfacedEventManager eventManager = new InterfacedEventManager();
    private final BiConsumer<GenericEvent, Throwable> exceptionHandler;

    public InterceptingEventManager(BiConsumer<GenericEvent, Throwable> exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public void register(Object listener) {
        eventManager.register(listener);
    }

    @Override
    public void unregister(Object listener) {
        eventManager.unregister(listener);
    }

    @Override
    public List<Object> getRegisteredListeners() {
        return eventManager.getRegisteredListeners();
    }

    @Override
    public void handle(GenericEvent event) {
        for (Object obj : getRegisteredListeners()) {
            if (!(obj instanceof EventListener listener)) continue;
            try {
                listener.onEvent(event);
            } catch (Throwable throwable) {
                exceptionHandler.accept(event, throwable);
            }
        }
    }
}
