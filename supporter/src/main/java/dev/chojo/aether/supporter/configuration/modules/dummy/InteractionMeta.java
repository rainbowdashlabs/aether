/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.supporter.configuration.modules.dummy;

import net.dv8tion.jda.api.interactions.commands.Command;

public record InteractionMeta(Command.Type type) {}
