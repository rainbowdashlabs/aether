/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.supporter.service.context;

import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public interface SupporterErrorSupplier {
    MessageCreateData getCommandError(AccessCheckResult result);

    String getAutoCompleteError();
}
