/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.commonweb.error;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Generic error response structure for API errors.
 */
public class ErrorResponseWrapper {
    private final String error;
    private final String message;
    private final Object details;

    public ErrorResponseWrapper(String error, String message, Object details) {
        this.error = error;
        this.message = message;
        this.details = details;
    }

    public ErrorResponseWrapper(String error, String message) {
        this(error, message, null);
    }

    @JsonProperty("error")
    public String error() {
        return error;
    }

    @JsonProperty("message")
    public String message() {
        return message;
    }

    @JsonProperty("details")
    public Object details() {
        return details;
    }
}
