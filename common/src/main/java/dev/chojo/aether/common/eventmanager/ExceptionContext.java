/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.common.eventmanager;

import net.dv8tion.jda.api.events.GenericEvent;

public record ExceptionContext(GenericEvent event) {}
