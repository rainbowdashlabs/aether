/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.discordoauth.access;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OAuthScopeTest {
    @Test
    void scopeNamesMatchDiscord() {
        assertEquals("applications.builds.read", OAuthScope.APPLICATIONS_BUILDS_READ.scope());
        assertEquals("applications.builds.upload", OAuthScope.APPLICATIONS_BUILDS_UPLOAD.scope());
        assertEquals("applications.commands", OAuthScope.APPLICATIONS_COMMANDS.scope());
        assertEquals(
                "applications.commands.permissions.update",
                OAuthScope.APPLICATIONS_COMMANDS_PERMISSIONS_UPDATE.scope());
        assertEquals("applications.entitlements", OAuthScope.APPLICATIONS_ENTITLEMENTS.scope());
        assertEquals("dm_channels.read", OAuthScope.DM_CHANNELS_READ.scope());
        assertEquals("guilds.members.read", OAuthScope.GUILDS_MEMBERS_READ.scope());
    }

    @Test
    void parsesOnlyTheNamedScopes() {
        assertEquals(Set.of(OAuthScope.GUILDS_JOIN, OAuthScope.IDENTIFY), OAuthScope.fromScope("guilds.join identify"));
        assertEquals(Set.of(OAuthScope.RPC_VOICE_READ), OAuthScope.fromScope("rpc.voice.read"));
    }
}
