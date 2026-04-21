/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.common.eventmanager;

public interface ExceptionHandler {
    void handle(Throwable err, ExceptionContext context);
}
