/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.supporter.service.context;

import dev.chojo.aether.supporter.configuration.modules.feature.AFeature;

import java.util.function.Function;

/**
 * Represents the result of an access check.
 *
 * @param feature   The feature that was checked.
 * @param hasAccess Whether the access is granted.
 */
public record AccessCheckResult(AFeature<?, ?> feature, boolean hasAccess) {
    /**
     * Creates a result with no specific enabling subscriptions.
     *
     * @param result The result.
     * @return The access check result.
     */
    public static AccessCheckResult hasAccess(AFeature<?, ?> feature, boolean result) {
        return new AccessCheckResult(feature, result);
    }

    /**
     * Creates a successful result.
     *
     * @return The access check result.
     */
    public static AccessCheckResult success(AFeature<?, ?> feature) {
        return new AccessCheckResult(feature, true);
    }

    /**
     * Creates a failed result with the given enabling subscriptions.
     *
     * @param feature The feature that failed the access check.
     * @return The access check result.
     */
    public static AccessCheckResult failure(AFeature<?, ?> feature) {
        return new AccessCheckResult(feature, false);
    }

    /**
     * Throws an exception if the access check failed.
     *
     * @param exception The exception to throw.
     * @param <T>       The type of exception to throw.
     */
    public <T extends RuntimeException> void accessOrThrow(Function<AFeature<?, ?>, T> exception) {
        if (!hasAccess()) throw exception.apply(feature);
    }
}
