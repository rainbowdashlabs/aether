/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.serialization;

import dev.chojo.aether.serialization.pojo.guild.MemberPOJO;
import dev.chojo.aether.serialization.pojo.guild.RolePOJO;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PojoSerializationTest {
    private final JsonMapper mapper = JsonMapper.builder().build();

    @Test
    void writesRoleWithDefaultMapper() {
        JsonNode node = mapper.valueToTree(new RolePOJO("Admin", "1", Color.RED, 3));
        assertEquals("Admin", node.get("name").asString());
        assertEquals("1", node.get("id").asString());
        assertEquals(3, node.get("position").asInt());
        assertEquals("#ff0000", node.get("color").asString());
    }

    @Test
    void writesMemberWithDefaultMapper() {
        JsonNode node = mapper.valueToTree(new MemberPOJO("Nora", "1", "#ffffff", "https://example.com/a.png"));
        assertEquals("Nora", node.get("displayName").asString());
        assertEquals("https://example.com/a.png", node.get("profilePictureUrl").asString());
    }

    @Test
    void unknownMemberGetsADefaultAvatar() {
        String id = "a";
        while (id.hashCode() >= 0) id += "a";
        JsonNode node = mapper.valueToTree(MemberPOJO.generate(id));
        assertTrue(node.get("profilePictureUrl")
                .asString()
                .matches("https://cdn\\.discordapp\\.com/embed/avatars/[0-4]\\.png"));
    }
}
