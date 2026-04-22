/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.discordoauth.pojo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ConnectionType {
    AMAZON_MUSIC("amazon-music", "Amazon Music"),
    BATTLENET("battlenet", "Battle.net"),
    BUNGIE("bungie", "Bungie.net"),
    BLUESKY("bluesky", "Bluesky"),
    CRUNCHYROLL("crunchyroll", "Crunchyroll"),
    DOMAIN("domain", "Domain"),
    EBAY("ebay", "eBay"),
    EPICGAMES("epicgames", "Epic Games"),
    FACEBOOK("facebook", "Facebook"),
    GITHUB("github", "GitHub"),
    INSTAGRAM("instagram", "Instagram"),
    LEAGUE_OF_LEGENDS("leagueoflegends", "League of Legends"),
    MASTODON("mastodon", "Mastodon"),
    PAYPAL("paypal", "PayPal"),
    PLAYSTATION("playstation", "PlayStation Network"),
    REDDIT("reddit", "Reddit"),
    RIOTGAMES("riotgames", "Riot Games"),
    ROBLOX("roblox", "Roblox"),
    SPOTIFY("spotify", "Spotify"),
    SKYPE("skype", "Skype"),
    STEAM("steam", "Steam"),
    TIKTOK("tiktok", "TikTok"),
    TWITCH("twitch", "Twitch"),
    TWITTER("twitter", "X (Twitter)"),
    XBOX("xbox", "Xbox"),
    YOUTUBE("youtube", "YouTube");

    private final String value;
    private final String name;

    ConnectionType(String value, String name) {
        this.value = value;
        this.name = name;
    }

    @JsonValue
    public String value() {
        return value;
    }

    public String getName() {
        return name;
    }

    @JsonCreator
    public static ConnectionType fromValue(String value) {
        for (ConnectionType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
}
