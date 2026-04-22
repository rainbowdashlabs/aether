/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.discordoauth.access;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum OAuthScope {
    /**
     * allows your app to fetch data from a user’s “Now Playing/Recently Played” list — not currently available for apps
     */
    ACTIVITIES_READ,

    /**
     * allows your app to update a user’s activity - not currently available for apps (NOT REQUIRED FOR GAMESDK ACTIVITY MANAGER)
     */
    ACTIVITIES_WRITE,

    /**
     * allows your app to read build data for a user’s applications
     */
    APPLICATIONS_BUILD_READ,

    /**
     * allows your app to upload/update builds for a user’s applications - only available to approved partners
     */
    APPLICATIONS_BUILD_UPDATE,

    /**
     * allows your app to add commands to a guild - included by default with the bot scope
     */
    APPPLICATIONS_COMMANDS,

    /**
     * allows your app to update its commands using a Bearer token - client credentials grant only
     */
    APPPLICATIONS_COMMANDS_UPDATE,
    /**
     * allows your app to update permissions for its commands in a guild a user has permissions to
     */
    APPPLICATIONS_COMMANDS_PERMISSIONS_UPDATE,

    /**
     * allows your app to read entitlements for a user’s applications
     */
    APPPLICATIONS_ENTITLEMENTS,

    /**
     * allows your app to read and update store data (SKUs, store listings, achievements, etc.) for a user’s applications
     */
    APPPLICATIONS_STORE_UPDATE,

    /**
     * for oauth2 bots, this puts the bot in the user’s selected guild by default
     */
    BOT,

    /**
     * allows /users/@me/connections to return linked third-party accounts
     */
    CONNECTIONS,

    /**
     * allows your app to see information about the user’s DMs and group DMs - only available to approved partners
     */
    DB_CHANNELS_READ("dm_channels.read"),

    /**
     * enables /users/@me to return an email
     */
    EMAIL,

    /**
     * allows your app to join users to a group dm
     */
    GDM_JOIN,

    /**
     * allows /users/@me/guilds to return basic information about all of a user’s guilds
     */
    GUILDS,

    /**
     * allows /guilds/{guild.id}/members/{user.id} to be used for joining users to a guild
     * In order to add a user to a guild, your bot has to already belong to that guild.
     */
    GUILDS_JOIN,

    /**
     * allows /users/@me/guilds/{guild.id}/member to return a user’s member information in a guild
     */
    GUILDS_MEMBERS_READ,

    /**
     * allows /users/@me without email
     */
    IDENTIFY,

    /**
     * for local rpc server api access, this allows you to read messages from all client channels (otherwise restricted to channels/guilds your app creates)
     */
    MESSAGES_READ,

    /**
     * Allows your app to access a user’s Discord Friends list, their pending requests, and blocked users. This scope is part of our Social SDK - submit for access here. Social SDK Terms apply, including Section 5(a)(ii) to the data you obtain
     */
    RELATIONSHIPS_READ,

    /**
     * allows your app to update a user’s connection and metadata for the app
     * Cannot be used with the Implicit grant type
     */
    ROLE_CONNECTIONS_WRITE("role_connections.write"),

    /**
     * for local rpc server access, this allows you to control a user’s local Discord client - only available to approved partners
     */
    RPC,

    /**
     * for local rpc server access, this allows you to update a user’s activity - only available to approved partners
     */
    RPC_ACTIVITIES_WRITE,

    /**
     * for local rpc server access, this allows you to receive notifications pushed out to the user - only available to approved partners
     */
    RPC_NOTIFICATIONS_READ,

    /**
     * for local rpc server access, this allows you to read a user’s voice settings and listen for voice events - only available to approved partners
     */
    RPC_VOICE_READ,

    /**
     * for local rpc server access, this allows you to update a user’s voice settings - only available to approved partners
     */
    RPC_VOICE_WRITE,

    /**
     * allows your app to connect to voice on user’s behalf and see all the voice members - only available to approved partners
     */
    VOICE,

    /**
     * this generates a webhook returned in the oauth token response for authorization code grants
     */
    WEBHOOK_INCOMING;

    private final String scope;

    OAuthScope(String scope) {
        this.scope = scope;
    }

    OAuthScope() {
        this.scope = this.name().toLowerCase().replace("_", ".");
    }

    public String scope() {
        return this.scope;
    }

    public boolean isContained(String scope) {
        return scope.contains(this.scope);
    }

    public static Set<OAuthScope> fromScope(String scope) {
        return Arrays.stream(values()).filter(s -> s.isContained(scope)).collect(Collectors.toSet());
    }
}
