# Mailing Module

The mailing module provides functionality for managing user email addresses, including registration, verification, and sending emails via SMTP.

## Features

- **Email Registration**: Register email addresses for users.
- **Verification**: Support for email verification via codes.
- **Email Sending**: Send HTML emails using SMTP.
- **Sent Storage**: Automatically store sent emails in the IMAP "Sent" folder.
- **Multi-Source**: Support for multiple registration sources (e.g., Discord, User, Ko-fi).
- **Hashed Storage**: Email addresses are stored as hashes to protect user privacy.

## Setup

To use the mailing service, you need to configure the `Mailing` class and provide it to the `MailService`.

### Configuration

The `Mailing` class requires SMTP and IMAP settings, along with credentials:

```java
Mailing mailing = new Mailing();
mailing.user("your-email@example.com");
mailing.password("your-password");

// SMTP Settings
mailing.smtp().host("smtp.example.com");
mailing.smtp().port(587);
mailing.smtp().ssl(true);

// IMAP Settings (for storing sent mails)
mailing.imap().host("imap.example.com");
mailing.imap().port(993);
mailing.imap().ssl(true);
```

### Initializing the Service

You need to implement `MailService` and provide a `MailServiceConfig`:

```java
MailServiceConfig config = new MailServiceConfig(
    "https://your-website.com",
    mailing,
    new MailTemplates(),
    userMailsProvider, // Your implementation of UserMailsProvider
    cleanupRunnable    // A runnable to clean up expired mails
);

MailService mailService = new YourMailServiceImpl(config);
```

## How it works

### Hashing
Email addresses are hashed using SHA-256 and a random salt generated upon initialization of the `Mailing` class (or provided via configuration). This ensures that the original email addresses are not stored directly if the implementation follows this pattern.

### Registration and Verification
1.  **`registerAndPromptVerify`**: Registers a mail address and sends a verification code to the user.
2.  **`verifyMail`**: Checks if a provided code matches the one sent to the user and marks the mail as verified.
3.  **`registerVerifiedMail`**: Directly registers a mail address as verified (useful for trusted sources like Ko-fi).

### Retrieval
User mails can be retrieved using the `UserMailsProvider`:

- **`byUser(long userId)`**: Retrieves all mail entries associated with a user ID.
- **`byHash(String mailHash)`**: Retrieves the mail entry (and its owner) for a specific mail hash.

Each user can have multiple mail addresses registered, managed via the `UserMails` interface.

### Templates
HTML templates are loaded from the resources (defaulting to the `mails/` directory). The `MailTemplates` class handles loading and replacing placeholders in these templates.
