---
title: Access checks
sidebar_position: 3
---

# Access checks

## The middleware

`SupporterMiddleware` is a jda-commands middleware. It runs before every interaction, builds the subscription context
and checks the [interaction rules](./configuration.md#interactions).

```java
SubcriptionContextProvider contexts = new SubcriptionContextProvider() {
    @Override
    public ISubscriptionContext getSubscriptionContext(User user) {
        return () -> repository.user(user.getIdLong()).subscriptionIds();
    }

    @Override
    public ISubscriptionContext getSubscriptionContext(Guild guild) {
        return () -> repository.guild(guild.getIdLong()).subscriptions().stream()
                .map(Subscription::subscriptionId)
                .collect(Collectors.toSet());
    }
};

ISupporterErrorSupplier errors = new ISupporterErrorSupplier() {
    @Override
    public MessageCreateData getCommandError(AccessCheckResult result) {
        return MessageCreateData.fromContent("This command needs a subscription.");
    }

    @Override
    public String getAutoCompleteError() {
        return "This command needs a subscription.";
    }
};

JDACommands.builder(jda)
        .middleware(Priority.HIGH, new SupporterMiddleware<>(contexts, configuration, errors))
        .start();
```

`ISubscriptionContext` has a single method, `enabledSubscriptions()`, so a lambda that returns a `Set<Long>` is
enough.

For each interaction the middleware:

1. collects the SKU ids of the Discord entitlements that come with the interaction,
2. adds the user's context, and in a guild the guild's context as well,
3. checks the command against the interaction rules.

So a rule or feature can list the subscription `id`, which your context provider returns, or a Discord SKU id, which
Discord sends with the interaction.

When the check fails, a command is cancelled and the user gets the message from `getCommandError`. An autocomplete
request gets an empty list of choices. `getAutoCompleteError` is not used at the moment.

## Checking features in a command

When the check passes, the middleware puts two values into the interaction's key-value store:

| Key | Value |
|---|---|
| `SupporterKeys.SUBSCRIPTION_VALIDATOR` | A `SupporterValidator` for your feature enum. |
| `SupporterKeys.SUBSCRIPTION_CONTEXT` | The `ISubscriptionContext` that was checked. |

Use the validator to check a feature inside a command:

```java
@Interaction
public class ProfileCommands {

    @Command("profile color")
    public void color(CommandEvent event, String color) {
        SupporterValidator<FeatureID> validator =
                event.kv().<SupporterValidator<FeatureID>>getOrThrow(SupporterKeys.SUBSCRIPTION_VALIDATOR);
        if (!validator.hasAccess(FeatureID.CUSTOM_PROFILE).hasAccess()) {
            event.reply("Custom colours need a subscription.");
            return;
        }
        event.reply("Your colour is now " + color + ".");
    }
}
```

`AccessCheckResult` holds the `feature` that was checked and whether the context `hasAccess`.
`accessOrThrow(feature -> new MyException(feature))` throws your exception when access is denied.

You can also check a feature directly: `context.hasAccess(configuration.feature(FeatureID.CUSTOM_PROFILE))`.
