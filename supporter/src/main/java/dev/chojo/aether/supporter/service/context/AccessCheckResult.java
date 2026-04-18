/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.supporter.service.context;

import java.util.Collections;
import java.util.Set;

/**
 * Represents the result of an access check.
 *
 * @param enabledBy A set of subscription IDs that would grant access.
 * @param hasAccess Whether the access is granted.
 */
public record AccessCheckResult(Set<Long> enabledBy, boolean hasAccess) {
    /**
     * Creates a result with no specific enabling subscriptions.
     * @param result The result.
     * @return The access check result.
     */
    public static AccessCheckResult hasAccess(boolean result) {
        return new AccessCheckResult(Set.of(), result);
    }

    /**
     * Creates a successful result.
     * @return The access check result.
     */
    public static AccessCheckResult success() {
        return new AccessCheckResult(Collections.emptySet(), true);
    }

    /**
     * Creates a failed result with the given enabling subscriptions.
     * @param enabledBy The set of enabling subscriptions.
     * @return The access check result.
     */
    public static AccessCheckResult failure(Set<Long> enabledBy) {
        return new AccessCheckResult(enabledBy, false);
    }
}
