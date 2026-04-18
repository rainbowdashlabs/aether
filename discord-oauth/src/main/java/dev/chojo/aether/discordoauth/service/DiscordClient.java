/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.discordoauth.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dev.chojo.aether.discordoauth.configuration.DiscordOAuth;
import dev.chojo.aether.discordoauth.pojo.DiscordGuild;
import dev.chojo.aether.discordoauth.pojo.DiscordUser;
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
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Client for interacting with the Discord API.
 */
public class DiscordClient {
    private static final String DISCORD_BASE = "https://discord.com";
    private static final String DISCORD_API = DISCORD_BASE + "/api/";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = JsonMapper.builder()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .build();
    private final DiscordOAuth configuration;

    private final Cache<String, DiscordUser> userCache =
            CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).build();
    private final Cache<String, List<DiscordGuild>> userGuildsCache =
            CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).build();

    /**
     * Creates a new Discord client.
     *
     * @param configuration The Discord OAuth configuration
     */
    public DiscordClient(DiscordOAuth configuration) {
        this.configuration = configuration;
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
    public String buildAuthorizeUrl(String state) {
        URIBuilder uriBuilder = new URIBuilder()
                .setHost(DISCORD_BASE)
                .setPathSegments("oauth2", "authorize")
                .addParameter("client_id", cfg().clientId())
                .addParameter("redirect_uri", cfg().redirectUri())
                .addParameter("response_type", "code")
                .addParameter("prompt", cfg().prompt().name().toLowerCase())
                .addParameter("scope", cfg().scopes());
        if (state != null) uriBuilder.addParameter("state", state);

        try {
            return uriBuilder.build().toString();
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
    public TokenResponse exchangeCode(String code) throws IOException, InterruptedException {
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
    public TokenResponse refreshToken(String refreshToken) throws IOException, InterruptedException {
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
     *
     * @param accessToken The access token
     * @return The user's information
     */
    public synchronized DiscordUser getCurrentUser(String accessToken) {
        try {
            return userCache.get(accessToken, () -> fetchUser(accessToken));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized DiscordUser fetchUser(String accessToken) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(DISCORD_API + "/users/@me"))
                .header("Authorization", "Bearer " + accessToken)
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
     *
     * @param accessToken The access token to use for the request
     * @return The user's guilds
     */
    public synchronized List<DiscordGuild> getUserGuilds(String accessToken) {
        try {
            return userGuildsCache.get(accessToken, () -> fetchGuilds(accessToken));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private List<DiscordGuild> fetchGuilds(String accessToken) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(DISCORD_API + "/users/@me/guilds"))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();
        HttpResponse<String> res = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() / 100 != 2) {
            throw new IOException("Discord /users/@me/guilds failed: %d - %s".formatted(res.statusCode(), res.body()));
        }
        List<DiscordGuild> guilds = mapper.readValue(res.body(), new TypeReference<>() {});
        userGuildsCache.put(accessToken, guilds);
        return guilds;
    }

    /**
     * Revokes a Discord OAuth token for the current application.
     * See https://discord.com/developers/docs/topics/oauth2#revoking-tokens
     *
     * @param accessToken The access token to revoke
     */
    public void revokeToken(String accessToken) throws IOException, InterruptedException {
        userCache.invalidate(accessToken);
        userGuildsCache.invalidate(accessToken);
        String form = "token=" + enc(accessToken);
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
            @JsonProperty("expires_in") long expiresIn) {
        public TokenResponse toTokenResponse() {
            return new TokenResponse(accessToken, refreshToken, Instant.now().plusSeconds(expiresIn));
        }
    }
}
