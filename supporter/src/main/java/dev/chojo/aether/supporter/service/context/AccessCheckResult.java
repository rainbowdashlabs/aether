/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.supporter.service.context;

import java.util.Collections;
import java.util.Set;

public record AccessCheckResult(Set<Long> enabledBy, boolean hasAccess) {
    public static AccessCheckResult hasAccess(boolean result) {
        return new AccessCheckResult(Set.of(), result);
    }

    public static AccessCheckResult success() {
        return new AccessCheckResult(Collections.emptySet(), true);
    }

    public static AccessCheckResult failure(Set<Long> enabledBy) {
        return new AccessCheckResult(enabledBy, false);
    }
}
