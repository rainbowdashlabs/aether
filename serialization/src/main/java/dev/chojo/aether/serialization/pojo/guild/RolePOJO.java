/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.serialization.pojo.guild;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.chojo.aether.common.util.Colors;
import net.dv8tion.jda.api.entities.Role;

import java.awt.*;
import java.util.Objects;

/**
 * POJO for {@link Role} serialization.
 */
@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal", "unused"})
public class RolePOJO {
    @JsonProperty
    private String name;

    @JsonProperty
    private String id;

    @JsonProperty
    private int position;

    @JsonProperty
    private String color;

    public RolePOJO(String name, String id, Color color, int position) {
        this.name = name;
        this.id = id;
        this.position = position;
        this.color = Colors.toHex(Objects.requireNonNullElse(color, Color.WHITE));
    }

    public static RolePOJO generate(Role role) {
        return new RolePOJO(role.getName(), role.getId(), role.getColors().getPrimary(), role.getPosition());
    }
}
