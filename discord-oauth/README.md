# Discord OAuth Module

[![Maven Central](https://img.shields.io/maven-central/v/dev.chojo.aether/discord-oauth)](https://central.sonatype.com/artifact/dev.chojo.aether/discord-oauth)

This module provides a simple way to integrate Discord OAuth2 authentication into a Javalin application.

## Installation

### Gradle (Kotlin DSL)

```kotlin
implementation("dev.chojo.aether:discord-oauth:version")
```

### Maven

```xml
<dependency>
    <groupId>dev.chojo.aether</groupId>
    <artifactId>discord-oauth</artifactId>
    <version>version</version>
</dependency>
```

## Features

- **OAuth2 Flow**: Handles the authorization code grant flow.
- **CSRF Protection**: Uses a short-lived cookie to validate the `state` parameter.
- **User Information**: Retrieve basic user information (ID, email).
- **Guild Information**: Retrieve the list of guilds the user is a member of.
- **Token Management**: Support for token exchange, refreshing, and revocation.
- **Caching**: In-memory caching for user and guild data to reduce API calls.

## Setup

### 1. Configuration

Define your Discord application credentials and settings using the `DiscordOAuth` class.

```java
DiscordOAuth config = new DiscordOAuth();
// Set your credentials (usually via a configuration loader)
// config.clientId = "your_client_id";
// config.clientSecret = "your_client_secret";
// config.redirectUri = "https://yourdomain.com/callback";
```

### 2. Implementation

Extend `DiscordOAuthService` to implement your custom logic for user persistence and token generation.

```java
public class MyDiscordOAuthService extends DiscordOAuthService {
    public MyDiscordOAuthService(DiscordClient discordClient, String host) {
        super(discordClient, host);
    }

    @Override
    public String userToken(long userId) {
        // Generate a session token for your application
        return MySessionManager.generateToken(userId);
    }

    @Override
    public void updateUser(DiscordUser user, TokenResponse token) {
        // Save or update user info and Discord tokens in your database
        MyDatabase.saveUser(user, token);
    }
}
```

### 3. Routing

Register the OAuth routes in your Javalin application.

```java
DiscordClient client = new DiscordClient(config);
DiscordOAuthService oauthService = new MyDiscordOAuthService(client, "https://your-frontend-host.com");

app.get("/login/discord", oauthService::startDiscordLogin);
app.get("/callback/discord", oauthService::handleDiscordCallback);
```

## How it works

### Retrieval

The `DiscordClient` provides methods to interact with the Discord API:

- `getCurrentUser(String accessToken)`: Fetches the `DiscordUser` associated with the token.
- `getUserGuilds(String accessToken)`: Fetches a list of `DiscordGuild` objects for the user.

Both methods utilize an internal cache (default 10 minutes) to avoid redundant network requests.

### Authentication Flow

1. **Login Start**: `startDiscordLogin` generates a random state, stores it in a secure cookie, and redirects the user to Discord's authorization page.
2. **Callback**: Discord redirects back to your application with a `code` and `state`.
3. **Validation**: `handleDiscordCallback` validates the `state` against the cookie to prevent CSRF.
4. **Exchange**: The `code` is exchanged for an access token and refresh token.
5. **Update**: `updateUser` is called with the user details, allowing you to persist them.
6. **Session**: `userToken` is called to get a token for your own application's session.
7. **Redirect**: The user is redirected to the configured `host` with your session token as a query parameter.
