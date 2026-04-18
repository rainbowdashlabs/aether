/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.mailing.util;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class Retry {
    public static <T> Optional<T> retryAndReturn(int tries, Callable<T> supplier, Consumer<Exception> onError) {
        int retries = 0;
        while (retries < tries) {
            retries++;
            try {
                return Optional.ofNullable(supplier.call());
            } catch (Exception e) {
                onError.accept(e);
            }
        }
        return Optional.empty();
    }

    public static boolean retryAndReturn(int tries, ThrowingRunnable run, Consumer<Exception> onError) {
        int retries = 0;
        while (retries < tries) {
            retries++;
            try {
                run.run();
                return true;
            } catch (Exception e) {
                onError.accept(e);
            }
        }
        return false;
    }
}
