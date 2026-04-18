/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.commonweb.util;

import java.nio.charset.StandardCharsets;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import static java.net.URLDecoder.decode;
import static java.util.stream.Collectors.toMap;

public class Urls {
    public static Map<String, String> splitQuery(String url) {
        return Arrays.stream(url.split("&"))
                .map(Urls::splitQueryParameter)
                .collect(toMap(Entry::getKey, Entry::getValue));
    }

    private static Entry<String, String> splitQueryParameter(String it) {
        final int idx = it.indexOf("=");
        final String key = idx > 0 ? it.substring(0, idx) : it;
        final String value = idx > 0 && it.length() > idx + 1 ? it.substring(idx + 1) : null;
        return new SimpleImmutableEntry<>(decode(key, StandardCharsets.UTF_8), decode(value, StandardCharsets.UTF_8));
    }
}
