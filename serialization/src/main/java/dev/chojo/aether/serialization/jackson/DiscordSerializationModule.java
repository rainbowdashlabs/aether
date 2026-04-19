/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.serialization.jackson;

import dev.chojo.aether.serialization.pojo.GuildPOJO;
import dev.chojo.aether.serialization.pojo.LocalePOJO;
import dev.chojo.aether.serialization.pojo.guild.MemberPOJO;
import dev.chojo.aether.serialization.pojo.guild.ReactionPOJO;
import dev.chojo.aether.serialization.pojo.guild.RolePOJO;
import dev.chojo.aether.serialization.pojo.guild.channel.CategoryPOJO;
import dev.chojo.aether.serialization.pojo.guild.channel.ChannelPOJO;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.StdSerializer;

/**
 * Jackson module for serializing Discord (JDA) entities.
 * <p>
 * This module registers serializers for common JDA entities like {@link Guild}, {@link Role},
 * {@link Member}, etc., converting them into POJOs suitable for JSON serialization.
 */
public class DiscordSerializationModule extends SimpleModule {
    public DiscordSerializationModule() {
        super("DiscordSerializationModule");
        addSerializer(new GuildSerializer());
        addSerializer(new RoleSerializer());
        addSerializer(new ReactionSerializer());
        addSerializer(new MemberSerializer());
        addSerializer(new CategorySerializer());
        addSerializer(new ChannelSerializer());
        addSerializer(new DiscordLocaleSerializer());
    }

    private static class GuildSerializer extends StdSerializer<Guild> {
        protected GuildSerializer() {
            super(Guild.class);
        }

        @Override
        public void serialize(Guild value, JsonGenerator gen, SerializationContext provider) throws JacksonException {
            gen.writePOJO(GuildPOJO.generate(value));
        }
    }

    private static class RoleSerializer extends StdSerializer<Role> {
        protected RoleSerializer() {
            super(Role.class);
        }

        @Override
        public void serialize(Role value, JsonGenerator gen, SerializationContext provider) throws JacksonException {
            gen.writePOJO(RolePOJO.generate(value));
        }
    }

    private static class ReactionSerializer extends StdSerializer<RichCustomEmoji> {
        protected ReactionSerializer() {
            super(RichCustomEmoji.class);
        }

        @Override
        public void serialize(RichCustomEmoji value, JsonGenerator gen, SerializationContext provider)
                throws JacksonException {
            gen.writePOJO(ReactionPOJO.generate(value));
        }
    }

    private static class MemberSerializer extends StdSerializer<Member> {
        protected MemberSerializer() {
            super(Member.class);
        }

        @Override
        public void serialize(Member value, JsonGenerator gen, SerializationContext provider) throws JacksonException {
            gen.writePOJO(MemberPOJO.generate(value));
        }
    }

    private static class CategorySerializer extends StdSerializer<Category> {
        protected CategorySerializer() {
            super(Category.class);
        }

        @Override
        public void serialize(Category value, JsonGenerator gen, SerializationContext provider)
                throws JacksonException {
            gen.writePOJO(CategoryPOJO.generate(value));
        }
    }

    private static class ChannelSerializer extends StdSerializer<GuildChannel> {
        protected ChannelSerializer() {
            super(GuildChannel.class);
        }

        @Override
        public void serialize(GuildChannel value, JsonGenerator gen, SerializationContext provider)
                throws JacksonException {
            gen.writePOJO(ChannelPOJO.from(value));
        }
    }

    private static class DiscordLocaleSerializer extends StdSerializer<DiscordLocale> {
        protected DiscordLocaleSerializer() {
            super(DiscordLocale.class);
        }

        @Override
        public void serialize(DiscordLocale value, JsonGenerator gen, SerializationContext provider)
                throws JacksonException {
            gen.writePOJO(LocalePOJO.of(value));
        }
    }
}
