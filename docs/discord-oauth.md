---
title: Discord OAuth
sidebar_position: 6
---

# Discord OAuth

The `discord-oauth` module logs users in with Discord in a [Javalin](https://javalin.io) web app. It runs the OAuth2
authorization code flow, refreshes tokens before they expire and calls the Discord API on the user's behalf.

```kotlin
implementation("dev.chojo.aether:discord-oauth:2.0.0")
```

## Configuration

`DiscordOAuth` holds the settings of your Discord application:

| Key | Default | Meaning |
|---|---|---|
| `clientId` | `""` | The client id of your Discord application. |
| `clientSecret` | `""` | Its client secret. |
| `redirectUri` | `""` | The callback URL. It must be listed as a redirect in the Discord developer portal. |
| `cacheRetentionMinutes` | `10` | How long user, guild and connection data stays cached. |
| `OAuthScopes` | `IDENTIFY`, `EMAIL`, `GUILDS` | The scopes to ask for. |
| `prompt` | `NONE` | `NONE` skips the consent screen for users who agreed before, `CONSENT` always shows it. |

```yaml
discordOAuth:
  clientId: "1234567890123456789"
  clientSecret: secret
  redirectUri: https://api.example.com/auth/callback
  OAuthScopes: [IDENTIFY, EMAIL, GUILDS]
```

The class keeps its values in private fields. Read it with a mapper that can see private fields, for example
[Ocular](/ocular).

## Your service

Extend `ADiscordOAuthService`. You implement two methods:

| Method | What it should do |
|---|---|
| `storeToken(user, token)` | Store the Discord token for the user and return a session token of your own. The user is sent back to your web app with it. |
| `getExpiringtokens(instant)` | Return the stored tokens that expire before the given time. |

Your stored token implements `IOAuthToken`: user id, access token, refresh token, scopes and expiry, plus
`update(TokenResponse)` to save a refreshed token and `delete()` to remove it.

```java
public class LoginService extends ADiscordOAuthService {
    private final TokenRepository tokens;

    public LoginService(DiscordOAuth config, TokenRepository tokens) {
        super(config, "https://dashboard.example.com");
        this.tokens = tokens;
    }

    @Override
    public String storeToken(DiscordUser user, TokenResponse token) {
        return tokens.save(user.id(), token).sessionToken();
    }

    @Override
    protected List<? extends IOAuthToken> getExpiringtokens(Instant instant) {
        return tokens.expiringBefore(instant);
    }
}
```

The second constructor argument is the base URL of your web app, where users land after the login.

The constructor starts the token refresh: two minutes after creation and then every 60 minutes, every token that
expires within the next hour is refreshed and saved with `update`. A token that can not be refreshed is deleted.

## Routes

Register the two handlers on your own routes. The callback route must match `redirectUri`:

```java
app.get("/auth/login", loginService::startDiscordLogin);
app.get("/auth/callback", loginService::handleDiscordCallback);
```

1. Your web app sends the user to `/auth/login?state=/settings`. The `state` parameter is the path to return to.
2. `startDiscordLogin` sets an `oauth_state` cookie (HttpOnly, five minutes) and redirects to Discord.
3. Discord sends the user to the callback. `handleDiscordCallback` checks the cookie, exchanges the code for a token,
   loads the user and calls `storeToken`.
4. The user is redirected to your web app at the requested path with `?token=<your session token>`.

Any error in the callback answers with status 400 and an `ErrorResponseWrapper` (see [Common web](./common-web.md)).

## Calling Discord

`client()` returns the `DiscordClient`. Each call takes the user's `IOAuthToken`:

| Method | Needs scope | Returns |
|---|---|---|
| `user(token)` | `IDENTIFY` (`EMAIL` for the address) | `DiscordUser` with id and mail address. |
| `guilds(token)` | `GUILDS` | The user's guilds as `DiscordGuild`. Empty without the scope. |
| `connections(token)` | `CONNECTIONS` | Linked accounts as `Connection`. Empty without the scope. |
| `guildMember(token, guildId)` | `GUILDS_MEMBERS_READ` | The user's `Member` in a guild, or `null`. |
| `joinGuild(data, token, botToken, guildId)` | `GUILDS_JOIN` | Adds the user to a guild your bot is in. Returns the `Member`, or `null`. |
| `revokeToken(token)` | | Revokes the token at Discord. |

`user`, `guilds` and `connections` are cached per access token for `cacheRetentionMinutes`.

:::warning

The scopes `APPPLICATIONS_COMMANDS`, `APPPLICATIONS_COMMANDS_UPDATE`, `APPPLICATIONS_COMMANDS_PERMISSIONS_UPDATE`,
`APPPLICATIONS_ENTITLEMENTS` and `APPPLICATIONS_STORE_UPDATE` produce scope names Discord does not know. Do not use
them. `joinGuild` sends its JSON body without a `Content-Type` header.

:::
