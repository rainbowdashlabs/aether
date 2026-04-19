/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.serialization.pojo;

import dev.chojo.aether.serialization.pojo.guild.ChannelViewPOJO;
import dev.chojo.aether.serialization.pojo.guild.ReactionPOJO;
import dev.chojo.aether.serialization.pojo.guild.RolePOJO;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import java.util.Comparator;
import java.util.List;

/**
 * POJO for {@link Guild} serialization.
 */
@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal", "unused"})
public final class GuildPOJO {
    private RolePOJO highestBotRole;
    private String name;
    private String id;
    private String iconUrl;
    private List<RolePOJO> roles;
    private ChannelViewPOJO channels;
    private List<ReactionPOJO> reactions;

    /**
     * @param highestBotRole the highest bot role in the guild
     * @param name           the name of the guild
     * @param id             the id of the guild
     * @param iconUrl        the url of the guild icon
     * @param roles          the roles in the guild
     * @param channels       the channels in the guild
     * @param reactions      the reactions in the guild
     */
    public GuildPOJO(
            RolePOJO highestBotRole,
            String name,
            String id,
            String iconUrl,
            List<RolePOJO> roles,
            ChannelViewPOJO channels,
            List<ReactionPOJO> reactions) {
        this.highestBotRole = highestBotRole;
        this.name = name;
        this.id = id;
        this.iconUrl = iconUrl;
        this.roles = roles;
        this.channels = channels;
        this.reactions = reactions;
    }

    public static GuildPOJO generate(Guild guild) {
        var selfMember = guild.getSelfMember();
        var highestRole = selfMember.getRoles().stream()
                .max(Comparator.comparingInt(Role::getPosition))
                .orElse(null);
        RolePOJO highestBotRole = highestRole != null ? RolePOJO.generate(highestRole) : null;

        List<RolePOJO> roles = guild.getRoles().stream()
                .filter(r -> !r.isPublicRole())
                .filter(r -> !r.isManaged())
                .map(RolePOJO::generate)
                .toList();
        List<ReactionPOJO> reactions = guild.retrieveEmojis().complete().stream()
                .map(ReactionPOJO::generate)
                .toList();
        return new GuildPOJO(
                highestBotRole,
                guild.getName(),
                guild.getId(),
                guild.getIconUrl(),
                roles,
                ChannelViewPOJO.generate(guild),
                reactions);
    }
}
