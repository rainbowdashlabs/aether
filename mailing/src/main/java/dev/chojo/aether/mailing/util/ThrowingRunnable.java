/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.mailing.util;

@FunctionalInterface
public interface ThrowingRunnable {
    void run() throws Exception;
}
