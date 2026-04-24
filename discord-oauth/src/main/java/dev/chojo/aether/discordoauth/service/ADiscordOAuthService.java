/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.discordoauth.service;

import dev.chojo.aether.commonweb.error.ErrorResponseWrapper;
import dev.chojo.aether.discordoauth.access.IOAuthToken;
import dev.chojo.aether.discordoauth.configuration.DiscordOAuth;
import dev.chojo.aether.discordoauth.pojo.DiscordUser;
import dev.chojo.aether.discordoauth.pojo.TokenResponse;
import io.javalin.http.Context;
import io.javalin.http.Cookie;
import io.javalin.http.HttpStatus;
import org.slf4j.Logger;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Base service for handling Discord OAuth2 flow with Javalin.
 */
public abstract class ADiscordOAuthService {
    private static final Logger log = getLogger(ADiscordOAuthService.class);
    private final DiscordClient discordClient;
    private final String host;

    /**
     * Creates a new Discord OAuth service.
     *
     * @param config The Discord OAuth configuration
     * @param host   The frontend host to redirect back to after login
     */
    public ADiscordOAuthService(DiscordOAuth config, String host) {
        this.discordClient = new DiscordClient(config);
        this.host = host;
        start();
    }

    /**
     * Starts the scheduled task {@link #refreshExpiredTokens()}
     */
    public void start() {
        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(this::refreshExpiredTokens, 2, 60, TimeUnit.MINUTES);
    }

    protected void refreshExpiredTokens() {
        log.info("Refreshing expired discord tokens");
        var expiringTokens = getExpiringtokens(Instant.now().plus(1, ChronoUnit.HOURS));
        for (var token : expiringTokens) {
            try {
                token.update(discordClient.refreshToken(token.refreshToken()));
                log.info("Refreshed discord access token for user {}", token.userId());
            } catch (Exception e) {
                token.delete();
                log.error("Failed to refresh discord access token for user {}", token.userId(), e);
            }
        }
    }

    /**
     * Get the tokens that will expire before the given instant.
     *
     * @param instant the instant to check
     * @return the tokens that will expire before the given instant
     * @see IOAuthToken
     */
    protected abstract List<? extends IOAuthToken> getExpiringtokens(Instant instant);

    /**
     * Starts the Discord login flow by redirecting the user to Discord.
     * <p>
     * A 'state' query parameter can be provided to be passed back after login.
     *
     * @param ctx The Javalin context
     */
    public void startDiscordLogin(Context ctx) {
        String next = ctx.queryParam("state");
        // Generate server-side random state to protect against CSRF
        String serverState = UUID.randomUUID().toString().replace("-", "");
        // Persist server state in a short-lived cookie for later validation
        Cookie cookie = new Cookie("oauth_state", serverState);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(300);
        ctx.cookie(cookie);
        // Compose final state: <serverState>::<nextPath>
        String composedState = serverState + "::" + (next != null ? next : "/");
        AuthorizeUrl url = discordClient.buildAuthorizeUrl(composedState);
        ctx.redirect(url.url());
    }

    /**
     * Handles the callback from Discord after the user has authorized the application.
     *
     * @param ctx The Javalin context
     */
    public void handleDiscordCallback(Context ctx) {
        String code = ctx.queryParam("code");
        String state = ctx.queryParam("state");
        if (code == null || code.isBlank()) {
            ctx.status(HttpStatus.BAD_REQUEST).json(new ErrorResponseWrapper("Invalid Request", "Missing code"));
            return;
        }
        if (state == null || state.isBlank()) {
            ctx.status(HttpStatus.BAD_REQUEST).json(new ErrorResponseWrapper("Invalid Request", "Missing state"));
            return;
        }
        try {
            // Validate state against cookie
            String cookieState = ctx.cookie("oauth_state");
            String serverState;
            String nextPath = "/";
            int idx = state.indexOf("::");
            if (idx > 0) {
                serverState = state.substring(0, idx);
                nextPath = state.substring(idx + 2);
            } else {
                serverState = state;
            }
            if (cookieState == null || !cookieState.equals(serverState)) {
                ctx.status(HttpStatus.BAD_REQUEST)
                        .json(new ErrorResponseWrapper("Invalid State", "OAuth state validation failed"));
                return;
            }

            var token = discordClient.exchangeCode(code);
            DiscordUser user = discordClient.user(token);
            String accessToken = storeToken(user, token);

            // Redirect back to the frontend with a session token
            if (nextPath.isBlank() || !nextPath.startsWith("/")) {
                nextPath = "/";
            }
            String separator = nextPath.contains("?") ? "&" : "?";
            String redirectUrl;
            if (host.endsWith("/") && nextPath.startsWith("/")) {
                redirectUrl = host + nextPath.substring(1) + separator + "token="
                        + URLEncoder.encode(accessToken, StandardCharsets.UTF_8);
            } else if (!host.endsWith("/") && !nextPath.startsWith("/")) {
                redirectUrl = host + "/" + nextPath + separator + "token="
                        + URLEncoder.encode(accessToken, StandardCharsets.UTF_8);
            } else {
                redirectUrl =
                        host + nextPath + separator + "token=" + URLEncoder.encode(accessToken, StandardCharsets.UTF_8);
            }
            // Clear state cookie
            ctx.removeCookie("oauth_state", "/");
            ctx.redirect(redirectUrl);
        } catch (Exception e) {
            log.error("Discord OAuth Flow Failed", e);
            ctx.status(HttpStatus.BAD_REQUEST).json(new ErrorResponseWrapper("Discord OAuth Failed", e.getMessage()));
        }
    }

    /**
     * Handle the user after an oauth token was issued.
     *
     * @param user  the user
     * @param token the token
     * @return An access token that can be used to authenticate the user in the backend.
     */
    public abstract String storeToken(DiscordUser user, TokenResponse token);

    /**
     * Get the Discord client used by this service.
     *
     * @return the Discord client
     */
    public DiscordClient client() {
        return discordClient;
    }
}
