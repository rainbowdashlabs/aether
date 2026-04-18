/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.mailing.entities;

public record Result<T, V extends Enum<?>>(T result, V failureReason, boolean success) {

    public Result(T result, V failureReason) {
        this(result, failureReason, failureReason == null);
    }

    public static <T, V extends Enum<?>> Result<T, V> failure(V failureReason) {
        return new Result<>(null, failureReason);
    }

    public static <T, V extends Enum<?>> Result<T, V> success(T result) {
        return new Result<>(result, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isFailure() {
        return !isSuccess();
    }
}
