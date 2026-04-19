/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.supporter.service.context;

import dev.chojo.aether.supporter.service.SupporterMiddleware;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

/**
 * Provides error messages for the {@link SupporterMiddleware}.
 */
public interface SupporterErrorSupplier {
    /**
     * Returns the error message for command interactions.
     *
     * @param result The result of the access check.
     * @return The error message.
     */
    MessageCreateData getCommandError(AccessCheckResult result);

    /**
     * Returns the error message for auto-complete interactions.
     * @return The error message.
     */
    String getAutoCompleteError();
}
