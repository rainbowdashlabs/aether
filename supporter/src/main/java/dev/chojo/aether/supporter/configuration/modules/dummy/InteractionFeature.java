/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.supporter.configuration.modules.dummy;

import dev.chojo.aether.supporter.configuration.modules.feature.Feature;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.Set;

public class InteractionFeature extends Feature<Empty, InteractionMeta> {
    public InteractionFeature(String name, Set<Long> enabledBy, Command.Type type) {
        super(-1, name, Empty.INSTANCE, new InteractionMeta(type), enabledBy);
    }
}
