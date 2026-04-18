# Aether

A library for Discord bots based on [JDA](https://github.com/discord-jda/JDA) and [jda-commands](https://github.com/kaktushose/jda-commands).

The Aether framework provides a set of modules designed to simplify common tasks for Discord bot developers, including user email management, Ko-fi integration, and flexible subscription/feature access control.

The project is available on **Maven Central**.

## Modules

### Common
The `common` module provides shared interfaces and utilities used across other Aether modules, such as `UserProvider` for abstracting Discord user and member lookups.

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

## Framework Overview

Aether is designed to be modular. You can use individual modules as needed, or combine them to build a comprehensive supporter and management system for your Discord bot. The framework focuses on decoupling logic from specific persistence layers, allowing you to implement your own storage solutions by extending the provided services and interfaces.
