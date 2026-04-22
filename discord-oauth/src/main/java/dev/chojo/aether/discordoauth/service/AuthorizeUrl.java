/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.discordoauth.service;

import dev.chojo.aether.discordoauth.access.OAuthScope;

import java.util.Set;

public record AuthorizeUrl(String url, Set<OAuthScope> OAuthScopes) {}
