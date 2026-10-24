---
title: Mailing
sidebar_position: 5
---

# Mailing

The `mailing` module links mail addresses to Discord users. It sends a confirmation mail with a code, verifies the
code and sends mails over SMTP. Addresses are stored only as a salted hash and a shortened form like
`jo***@***il.com`.

```kotlin
implementation("dev.chojo.aether:mailing:2.0.0")
```

## Configuration

`Mailing` holds the mail server settings:

| Key | Default | Meaning |
|---|---|---|
| `mailSalt` | random on every start | Salt for the address hashes. Set it, or stored hashes stop matching after a restart. |
| `smtp.host`, `smtp.port`, `smtp.ssl` | `""`, `665`, `false` | The SMTP server for sending. |
| `imap.host`, `imap.port`, `imap.ssl` | `""`, `665`, `false` | The IMAP server, where a copy of each sent mail is stored. |
| `user` | `""` | Login and sender address. |
| `password` | `""` | Password for SMTP and IMAP. |
| `properties` | empty | Extra Jakarta Mail properties, for example `mail.smtp.auth: "true"`. |

```yaml
mailing:
  mailSalt: a-long-random-string
  smtp:
    host: smtp.example.com
    port: 465
    ssl: true
  imap:
    host: imap.example.com
    port: 993
    ssl: true
  user: bot@example.com
  password: secret
```

Like the supporter configuration, these classes keep their values in private fields. Read them with a mapper that
can see private fields, for example [Ocular](/ocular).

## Templates

You provide the mail texts as HTML files on the class path, under `mails/`:

| File | Sent when | Placeholders |
|---|---|---|
| `mails/account_confirmation.html` | A user registers an address. | `{{ url }}` is the confirmation link, `{{ mail }}` the address. |
| `mails/link_account.html` | Someone buys on Ko-fi with an unknown address and no Discord account. | `{{ mail }}` is the address, `{{ host }}` your host. |

The confirmation link is `<host>/user/settings?code=<code>&hash=<hash>`. Your web app needs a page at that path that
reads the two parameters and calls `verifyMail`.

## Your classes

| Type | What it is |
|---|---|
| `AMailEntry` | One stored address. You implement `verify()`, `regenerateVerificationCode()` and `updateUser(userId)`, which save the change. |
| `IUserMails` | The addresses of one user: `getMail(hash)`, `addMail(entry)`, `removeMail(hash)`, `mails()`, `userId()`. |
| `IUserMailsProvider` | Finds the `IUserMails` of a user (`byUser`) or of an address hash (`byHash`). |
| `AMailService` | The service. You implement `createMailEntry(...)`, which creates your entry class. |

```java
public class MailService extends AMailService<UserMail> {
    public MailService(MailServiceConfig<UserMail> config) {
        super(config);
    }

    @Override
    protected UserMail createMailEntry(long user, MailSource source, String mailHash, String mailShort,
            boolean verified, Instant verificationRequested, String verificationCode) {
        return new UserMail(user, source, mailHash, mailShort, verified, verificationRequested, verificationCode);
    }
}

MailService mailService = new MailService(new MailServiceConfig<>(
        "https://bot.example.com",
        mailing,
        new MailTemplates(),
        userMailsProvider,
        repository::deleteUnverifiedMails));
```

`MailServiceConfig` takes the public host of your web app, the `Mailing` configuration, the templates, your
`IUserMailsProvider` and a cleanup task. The service runs the cleanup every 60 minutes, starting 30 minutes after it
was created.

## Register and verify an address

```java
Result<UserMail, FailureReason> result = mailService.registerAndPromptVerify(userId, address, MailSource.USER);
if (result.isFailure()) {
    reply("That did not work: " + result.failureReason());
}
```

`registerAndPromptVerify` checks the address, stores a new entry through your `IUserMails` and sends the confirmation
mail in the background. For an address that is already known but not verified, the service calls `updateUser` on the
existing entry with the new user and sends the mail again. It fails with:

| Reason | When |
|---|---|
| `INVALID_FORMAT` | The address is not valid. |
| `ALREADY_REGISTERED` | The address is verified already. |

When the user opens the link, call `verifyMail(userId, hash, code)`. It returns `null` on success, otherwise:

| Reason | When |
|---|---|
| `UNKNOWN_ADDRESS` | No entry has this hash. |
| `WRONG_USER` | The entry belongs to another user. |
| `INVALID_CODE` | The code does not match. |
| `CODE_EXPIRED` | The code is older than one hour. |

`getRetryAfterSeconds(userId)` tells how many seconds are left of a five minute wait since the user's last request.
The service does not enforce the wait; check it before calling `registerAndPromptVerify`.

`registerVerifiedMail(userId, address, source)` stores an address as verified right away, without a mail. The Ko-fi
module uses it for buyers whose Discord account is known.

## Mail sources

`MailSource` tells where an address came from: `DISCORD`, `USER` or `KOFI`. Add your own with `MailSource.of(name)`
and register it in `MailSourceRegistry.INSTANCE`.

## Sending other mails

`sendMail(new Mail(address, subject, html))` sends any HTML mail. It sends over SMTP and then stores a copy in the
folder `Sent` below the IMAP inbox.
