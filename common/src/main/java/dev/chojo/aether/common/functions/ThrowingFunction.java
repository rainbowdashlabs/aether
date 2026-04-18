/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.common.functions;

public interface ThrowingFunction<R, T, E extends Exception> {
    R apply(T t) throws E;
}
