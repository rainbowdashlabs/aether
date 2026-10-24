/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.commonweb.util;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UrlsTest {
    @Test
    void decodesParameters() {
        assertEquals(Map.of("a", "1", "b", "x y"), Urls.splitQuery("a=1&b=x%20y"));
    }

    @Test
    void parameterWithoutValueIsEmpty() {
        assertEquals(Map.of("a", "", "b", "2"), Urls.splitQuery("a&b=2"));
    }

    @Test
    void repeatedParameterKeepsLastValue() {
        assertEquals(Map.of("a", "2"), Urls.splitQuery("a=1&a=2"));
    }
}
