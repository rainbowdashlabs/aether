---
title: Configuration
sidebar_position: 2
---

# Configuration

`SupporterConfiguration<FeatureID, Price, FeatureMeta>` holds your subscriptions, features and locked commands. It is
meant to be read from a configuration file with Jackson, for example with [Ocular](/ocular).

The type parameters are yours:

- `FeatureID` is an enum with one constant per feature.
- `Price` and `FeatureMeta` are any classes for extra data on a feature, for example a price to show in a shop. Use
  `Empty` when you need none.

## An example

```yaml
subscriptions:
  SUPPORTER:
    name: Supporter
    id: 1
    platforms:
      DISCORD:
        ids:
          SUBSCRIPTION: "1234567890123456789"
          LIFETIME: "1234567890123456790"
      KOFI:
        ids:
          SUBSCRIPTION: Supporter
          LIFETIME: a1b2c3d4e5
features:
  CUSTOM_PROFILE:
    id: 10
    name: Custom profile
    price: 5
    meta: ""
    enabledBy: [1]
interactions:
  slash:
    profile edit: [1]
  messages: {}
  users: {}
```

### `subscriptions`

A map from a subscription key to a subscription.

| Key | Meaning |
|---|---|
| `name` | A readable name. |
| `id` | A number that identifies the subscription. Features and commands refer to it. Never change it once in use. |
| `platforms` | Per platform, the `ids` of the subscription for each purchase type. |

The platform ids are:

- Discord: the SKU id of the subscription or one-time purchase.
- Ko-fi: the tier name for `SUBSCRIPTION`, the shop item's direct link code for `LIFETIME`.

### `features`

A map from your `FeatureID` constant to a feature.

| Key | Meaning |
|---|---|
| `id` | A number for the feature. A subscription context that contains this id unlocks the feature as well. |
| `name` | A readable name. |
| `price`, `meta` | Your extra data, read as `Price` and `FeatureMeta`. |
| `enabledBy` | The ids that unlock the feature. Empty means everyone may use it. |

### `interactions`

Commands that need a subscription. `slash` holds slash commands, `messages` message context commands and `users` user
context commands. The key is the full command name as Discord reports it, with subcommand group and subcommand
separated by spaces (`profile edit`). The value lists the ids that unlock the command. Commands that are not listed
are open to everyone.

## Reading the configuration

Three things must be in place before Jackson reads the file:

1. **The `SupporterModule` is registered** on the mapper. It reads and writes platforms, purchase types and
   subscription keys by their names.
2. **Every subscription key is registered.** Platforms and purchase types are registered already, subscription keys
   are not. A key that is not registered fails to read.
3. **Jackson can see private fields** and knows your feature class. The configuration classes keep their values in
   private fields without setters, and `AFeature` is abstract.

```java
SupporterRegistry.SUBSCRIPTION_TYPES.register(new SubscriptionKey("SUPPORTER"));

JsonMapper mapper = JsonMapper.builder()
        .addModule(new SupporterModule())
        .addModule(new SimpleModule().addAbstractTypeMapping(AFeature.class, ProfileFeature.class))
        .changeDefaultVisibility(checker -> checker.withFieldVisibility(JsonAutoDetect.Visibility.ANY))
        .build();
```

Ocular already makes private fields visible. Pass the two modules to it instead.

Your feature class extends `AFeature` and needs a constructor without arguments for Jackson:

```java
public class ProfileFeature extends AFeature<Integer, String> {
    public ProfileFeature() {
        super(0, 0, "", Set.of());
    }
}
```

## Looking things up

| Method | Returns |
|---|---|
| `feature(featureId)` | The feature for an id of your enum. |
| `subscription(key)` | The subscription for a key. |
| `subscriptions()` | All subscriptions. |
| `byId(id)` | The subscription with that numeric id. |
| `findSubscription(platform, purchaseType, platformId)` | The subscription with that platform id for that purchase type. |
| `findSubscriptionBySkuID(platform, sku)` | The subscription that lists that id for any purchase type. |
| `findPurchaseTypeBySkuID(platform, sku)` | The purchase type an id stands for. |
| `interactions()` | The interaction rules. |

:::warning

The lookups by platform fail with a `NullPointerException` when a subscription has no entry for the platform you ask
for, and `findSubscription` also when it has no id for the purchase type. Give every subscription an id for each
platform and purchase type you use.

:::
