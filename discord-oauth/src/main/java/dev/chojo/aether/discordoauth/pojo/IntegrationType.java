/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.discordoauth.pojo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum IntegrationType {
    TWITCH("twitch"),
    YOUTUBE("youtube"),
    DISCORD("discord"),
    GUILD_SUBSCRIPTION("guild_subscription");

    private final String value;

    IntegrationType(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static IntegrationType fromValue(String value) {
        for (IntegrationType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
}
