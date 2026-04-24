/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.discordoauth.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dev.chojo.aether.discordoauth.access.IOAuthToken;
import dev.chojo.aether.discordoauth.access.OAuthScope;
import dev.chojo.aether.discordoauth.configuration.DiscordOAuth;
import dev.chojo.aether.discordoauth.pojo.Connection;
import dev.chojo.aether.discordoauth.pojo.DiscordGuild;
import dev.chojo.aether.discordoauth.pojo.DiscordUser;
import dev.chojo.aether.discordoauth.pojo.JoinGuildData;
import dev.chojo.aether.discordoauth.pojo.Member;
import dev.chojo.aether.discordoauth.pojo.TokenResponse;
import org.apache.hc.core5.net.URIBuilder;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Client for interacting with the Discord API.
 */
public class DiscordClient {
    private static final String DISCORD_BASE = "discord.com";
    private static final String DISCORD_API = "https://" + DISCORD_BASE + "/api/";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = JsonMapper.builder()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .build();
    private final DiscordOAuth configuration;

    private final Cache<String, DiscordUser> userCache;
    private final Cache<String, List<DiscordGuild>> userGuildsCache;
    private final Cache<String, List<Connection>> userConnectionsCache;

    /**
     * Creates a new Discord client.
     *
     * @param configuration The Discord OAuth configuration
     */
    public DiscordClient(DiscordOAuth configuration) {
        this.configuration = configuration;
        userCache = createCache();
        userGuildsCache = createCache();
        userConnectionsCache = createCache();
    }

    private <K, V> Cache<K, V> createCache() {
        return CacheBuilder.newBuilder()
                .expireAfterWrite(configuration.cacheRetentionMinutes(), TimeUnit.MINUTES)
                .build();
    }

    private static String enc(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    /**
     * Builds the authorization URL for the user to login.
     *
     * @param state The state to include in the URL for CSRF protection and context
     * @return The authorization URL
     */
    AuthorizeUrl buildAuthorizeUrl(String state) {
        return buildAuthorizeUrl(state, cfg().scopes());
    }

    AuthorizeUrl buildAuthorizeUrl(String state, Set<OAuthScope> OAuthScopes) {
        URIBuilder uriBuilder = new URIBuilder()
                .setScheme("https")
                .setHost(DISCORD_BASE)
                .setPathSegments("oauth2", "authorize")
                .addParameter("client_id", cfg().clientId())
                .addParameter("redirect_uri", cfg().redirectUri())
                .addParameter("response_type", "code")
                .addParameter("prompt", cfg().prompt().name().toLowerCase())
                .addParameter(
                        "scope", OAuthScopes.stream().map(OAuthScope::scope).collect(Collectors.joining(" ")));
        if (state != null) uriBuilder.addParameter("state", state);

        try {
            return new AuthorizeUrl(uriBuilder.build().toString(), OAuthScopes);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Exchanges an authorization code for an access token.
     *
     * @param code The authorization code
     * @return The token response
     * @throws IOException          If the exchange fails
     * @throws InterruptedException If the request is interrupted
     */
    TokenResponse exchangeCode(String code) throws IOException, InterruptedException {
        String form =
                "grant_type=authorization_code&code=%s&redirect_uri=%s".formatted(enc(code), enc(cfg().redirectUri()));
        return exchange(form);
    }

    /**
     * Refreshes an access token using a refresh token.
     *
     * @param refreshToken The refresh token
     * @return The new token response
     * @throws IOException          If the refresh fails
     * @throws InterruptedException If the request is interrupted
     */
    TokenResponse refreshToken(String refreshToken) throws IOException, InterruptedException {
        String form = "grant_type=refresh_token&refresh_token=%s".formatted(enc(refreshToken));
        return exchange(form);
    }

    private TokenResponse exchange(String form) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(DISCORD_API + "/oauth2/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", basicAuth(cfg().clientId(), cfg().clientSecret()))
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() / 100 != 2) {
            throw new IOException("Discord token exchange failed: " + response.statusCode() + " - " + response.body());
        }
        return mapper.readValue(response.body(), DiscordTokenResponse.class).toTokenResponse();
    }

    /**
     * Fetches the current user's information.
     * <p>
     * Requires the {@link OAuthScope#IDENTIFY} scope.
     * If the {@link OAuthScope#EMAIL} scope is provided, the email field will be populated.
     *
     * @param token The access token
     * @return The user's information
     */
    public DiscordUser user(IOAuthToken token) {
        try {
            return userCache.get(token.accessToken(), () -> fetchUser(token));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private DiscordUser fetchUser(IOAuthToken accessToken) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(DISCORD_API + "/users/@me"))
                .header("Authorization", "Bearer " + accessToken.accessToken())
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() / 100 != 2) {
            throw new IOException("Discord /users/@me failed: " + response.statusCode() + " - " + response.body());
        }
        return mapper.readValue(response.body(), DiscordUser.class);
    }

    /**
     * Fetches the user's guilds.
     * <p>
     * Requires the {@link OAuthScope#GUILDS} scope.
     *
     * @param token The access token to use for the request
     * @return A list of the user's guilds, or an empty list if the scope is missing.
     */
    public List<DiscordGuild> guilds(IOAuthToken token) {
        if (!token.hasScope(OAuthScope.GUILDS)) return Collections.emptyList();
        try {
            return userGuildsCache.get(token.accessToken(), () -> fetchGuilds(token));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Fetches the user's third-party connections.
     * <p>
     * Requires the {@link OAuthScope#CONNECTIONS} scope.
     *
     * @param token The access token to use for the request
     * @return A list of the user's connections, or an empty list if the scope is missing.
     */
    public List<Connection> connections(IOAuthToken token) {
        if (!token.hasScope(OAuthScope.CONNECTIONS)) return Collections.emptyList();
        try {
            return userConnectionsCache.get(token.accessToken(), () -> fetchConnections(token));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Joins a user to a guild.
     * <p>
     * Requires the {@link OAuthScope#GUILDS_JOIN} scope.
     *
     * @param data     Data to join guild with
     * @param token    The user's access token
     * @param botToken Bot token for authorization
     * @param guildId  ID of the guild to join
     */
    public Member joinGuild(JoinGuildData data, IOAuthToken token, String botToken, long guildId)
            throws IOException, InterruptedException {
        if (!token.hasScope(OAuthScope.GUILDS_JOIN)) {
            return null;
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(DISCORD_API + "/guilds/" + guildId + "/members/" + token.userId()))
                .header("Authorization", "Bot " + botToken)
                .PUT(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(data.withToken(token))))
                .build();
        HttpResponse<String> res = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() / 100 != 2) {
            return null;
        }
        return mapper.readValue(res.body(), Member.class);
    }

    /**
     * Fetches the user's member information for a specific guild.
     * <p>
     * Requires the {@link OAuthScope#GUILDS_MEMBERS_READ} scope.
     *
     * @param token   The user's access token
     * @param guildId ID of the guild
     * @return The member information, or {@code null} if the scope is missing or the user is not in the guild.
     * @throws IOException          If the request fails
     * @throws InterruptedException If the request is interrupted
     */
    public Member guildMember(IOAuthToken token, long guildId) throws IOException, InterruptedException {
        if (!token.hasScope(OAuthScope.GUILDS_MEMBERS_READ)) {
            return null;
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(DISCORD_API + "/users/@me/guilds/" + guildId + "/member"))
                .header("Authorization", "Bearer " + token.accessToken())
                .GET()
                .build();
        HttpResponse<String> res = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() / 100 != 2) {
            return null;
        }
        return mapper.readValue(res.body(), Member.class);
    }

    private List<Connection> fetchConnections(IOAuthToken token) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(DISCORD_API + "/users/@me/connections"))
                .header("Authorization", "Bearer " + token.accessToken())
                .GET()
                .build();
        HttpResponse<String> res = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() / 100 != 2) {
            throw new IOException(
                    "Discord /users/@me/connections failed: %d - %s".formatted(res.statusCode(), res.body()));
        }
        return mapper.readValue(res.body(), new TypeReference<>() {});
    }

    private List<DiscordGuild> fetchGuilds(IOAuthToken token) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(DISCORD_API + "/users/@me/guilds"))
                .header("Authorization", "Bearer " + token.accessToken())
                .GET()
                .build();
        HttpResponse<String> res = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() / 100 != 2) {
            throw new IOException("Discord /users/@me/guilds failed: %d - %s".formatted(res.statusCode(), res.body()));
        }
        return mapper.readValue(res.body(), new TypeReference<>() {});
    }

    /**
     * Revokes a Discord OAuth token for the current application.
     * See <a href="https://discord.com/developers/docs/topics/oauth2#revoking-tokens">Discord Docs</a>
     *
     * @param token The access token to revoke
     */
    void revokeToken(IOAuthToken token) throws IOException, InterruptedException {
        userCache.invalidate(token.accessToken());
        userGuildsCache.invalidate(token.accessToken());
        String form = "token=" + enc(token.accessToken());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(DISCORD_API + "/oauth2/token/revoke"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", basicAuth(cfg().clientId(), cfg().clientSecret()))
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();
        HttpResponse<String> res = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() / 100 != 2) {
            throw new IOException("Discord token revoke failed: %d - %s".formatted(res.statusCode(), res.body()));
        }
    }

    private String basicAuth(String username, String password) {
        String auth = username + ":" + password;
        return "Basic " + java.util.Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
    }

    private DiscordOAuth cfg() {
        return configuration;
    }

    private record DiscordTokenResponse(
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("refresh_token") String refreshToken,
            @JsonProperty("expires_in") long expiresIn,
            @JsonProperty("scope") String scope) {
        public TokenResponse toTokenResponse() {
            return new TokenResponse(
                    accessToken, refreshToken, Instant.now().plusSeconds(expiresIn), OAuthScope.fromScope(scope));
        }
    }
}
