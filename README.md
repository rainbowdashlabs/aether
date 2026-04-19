# Aether

[![Maven Central](https://img.shields.io/maven-central/v/dev.chojo.aether/aether)](https://central.sonatype.com/artifact/dev.chojo.aether/aether)
[![License](https://img.shields.io/github/license/rainbowdashlabs/aether)](https://github.com/rainbowdashlabs/aether/blob/master/LICENSE.md)
[![Build Status](https://github.com/rainbowdashlabs/aether/actions/workflows/verify.yml/badge.svg)](https://github.com/rainbowdashlabs/aether/actions/workflows/verify.yml)
[![Javadocs Status](https://github.com/rainbowdashlabs/aether/actions/workflows/javadocs.yml/badge.svg)](https://github.com/rainbowdashlabs/aether/actions/workflows/javadocs.yml)
[![Last Commit](https://img.shields.io/github/last-commit/rainbowdashlabs/aether)](https://github.com/rainbowdashlabs/aether/commits/master)
[![Issues](https://img.shields.io/github/issues/rainbowdashlabs/aether)](https://github.com/rainbowdashlabs/aether/issues)

A library for Discord bots based on [JDA](https://github.com/discord-jda/JDA) and [jda-commands](https://github.com/kaktushose/jda-commands).

The Aether framework provides a set of modules designed to simplify common tasks for Discord bot developers, including user email management, Ko-fi integration, flexible subscription/feature access control, and seamless Discord OAuth2 authentication.

The project is available on **Maven Central**.

## Installation

### Gradle (Kotlin DSL)

```kotlin
implementation("dev.chojo.aether:aether:version")
```

### Maven

```xml
<dependency>
    <groupId>dev.chojo.aether</groupId>
    <artifactId>aether</artifactId>
    <version>version</version>
</dependency>
```

## Modules

### Common
The `common` module provides shared interfaces and utilities used across other Aether modules, such as `UserProvider` for abstracting Discord user and member lookups.

### Common Web
The `common-web` module provides web-related utilities and error handling for Discord-related web services, including common URL management and exception handling.

### Mailing
The `mailing` module handles user email management. It supports:
- **Registration & Verification**: Securely register and verify user email addresses via SMTP/IMAP.
- **Privacy**: Email addresses are stored as hashes to protect user privacy.
- **Templates**: Integrated support for HTML email templates.

### Ko-fi
The `kofi` module provides seamless integration with [Ko-fi](https://ko-fi.com) via webhooks. It allows:
- **Webhook Handling**: Receive and validate Ko-fi notifications for donations, subscriptions, and shop orders.
- **User Mapping**: Automatically link Ko-fi transactions to Discord users.
- **Subscription Management**: Track and manage user-assigned guild benefits.

### Supporter
The `supporter` module offers a flexible system for managing and validating user and guild subscriptions and features. It includes:
- **Access Control**: Middleware-based access validation for Discord interactions (Slash Commands, Context Menus).
- **Extensibility**: Support for multiple platforms (e.g., Discord, Ko-fi) and custom subscription tiers.
- **JDAC Integration**: Seamlessly integrates with the JDA Commands (JDAC) framework for automated permission checks.

### Discord OAuth
The `discord-oauth` module provides a simple way to integrate Discord OAuth2 authentication into a Javalin application, handling the authorization code grant flow, token management, and retrieval of user and guild information.

### Serialization
The `serialization` module provides Jackson serializers for [JDA](https://github.com/discord-jda/JDA) entities, converting them into POJOs that can be easily serialized to JSON.

## Framework Overview

Aether is designed to be modular. You can use individual modules as needed, or combine them to build a comprehensive supporter and management system for your Discord bot. The framework focuses on decoupling logic from specific persistence layers, allowing you to implement your own storage solutions by extending the provided services and interfaces.
