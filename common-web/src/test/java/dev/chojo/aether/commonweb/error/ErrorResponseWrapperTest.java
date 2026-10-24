/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.commonweb.error;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorResponseWrapperTest {
    @Test
    void writesAllFieldsWithDefaultMapper() {
        String json =
                JsonMapper.builder().build().writeValueAsString(new ErrorResponseWrapper("Not Found", "Unknown guild"));
        assertEquals("{\"details\":null,\"error\":\"Not Found\",\"message\":\"Unknown guild\"}", json);
    }
}
