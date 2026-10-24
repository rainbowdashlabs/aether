---
title: Common web
sidebar_position: 8
---

# Common web

The `common-web` module holds small parts for Javalin web apps. [Discord OAuth](./discord-oauth.md) uses them.

```kotlin
implementation("dev.chojo.aether:common-web:2.0.0")
```

## Errors

`ApiException` is a runtime exception with an HTTP status. Throw it in a handler and turn it into a response in one
place:

```java
app.exception(ApiException.class, (e, ctx) ->
        ctx.status(e.status()).json(new ErrorResponseWrapper(e.status().getMessage(), e.getMessage())));

app.get("/guilds/{id}", ctx -> {
    Guild guild = jda.getGuildById(ctx.pathParam("id"));
    if (guild == null) throw new ApiException(HttpStatus.NOT_FOUND, "Unknown guild");
    ctx.json(guild);
});
```

`ErrorResponseWrapper` holds an `error` title, a `message` and optional `details`. Its values are private fields
read through `error()`, `message()` and `details()`, so the JSON mapper has to see private fields to write them.

## Query strings

`Urls.splitQuery("a=1&b=2")` splits a query string into a map and decodes the keys and values.

:::warning

`splitQuery` fails on a parameter without a value (`a&b=2`) and on a parameter that appears twice.

:::
