/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.mailing.entities;

/**
 * Represents a result of an operation that can fail with a reason.
 *
 * @param result        the result of the operation
 * @param failureReason the reason for failure, or null if successful
 * @param success       true if the operation was successful
 * @param <T>           the type of the result
 * @param <V>           the type of the failure reason
 */
public record Result<T, V extends Enum<?>>(T result, V failureReason, boolean success) {

    /**
     * Create a new result.
     *
     * @param result        the result
     * @param failureReason the failure reason
     */
    public Result(T result, V failureReason) {
        this(result, failureReason, failureReason == null);
    }

    /**
     * Create a failure result.
     *
     * @param failureReason the failure reason
     * @param <T>           the result type
     * @param <V>           the failure reason type
     * @return the failure result
     */
    public static <T, V extends Enum<?>> Result<T, V> failure(V failureReason) {
        return new Result<>(null, failureReason);
    }

    /**
     * Create a success result.
     *
     * @param result the result
     * @param <T>    the result type
     * @param <V>    the failure reason type
     * @return the success result
     */
    public static <T, V extends Enum<?>> Result<T, V> success(T result) {
        return new Result<>(result, null);
    }

    /**
     * Check if the operation was successful.
     *
     * @return true if successful
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Check if the operation failed.
     *
     * @return true if failed
     */
    public boolean isFailure() {
        return !isSuccess();
    }
}
