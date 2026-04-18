package dev.chojo.aether.common;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.util.Optional;

public interface UserProvider {
    Optional<User> byId(long id);

    Optional<User> byId(String id);

    Optional<Member> byId(long id, long guildId);

    Optional<Member> byId(long id, Guild guild);
}
