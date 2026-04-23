/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.discordoauth.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.chojo.aether.discordoauth.access.IOAuthToken;

import java.util.List;

public record JoinGuildPayload(
        @JsonProperty("access_token") String accessToken, String nick, List<String> roles, boolean mute, boolean deaf) {
    public JoinGuildPayload(IOAuthToken token, String nick, List<String> roles, boolean mute, boolean deaf) {
        this(token.accessToken(), nick, roles, mute, deaf);
    }
}
