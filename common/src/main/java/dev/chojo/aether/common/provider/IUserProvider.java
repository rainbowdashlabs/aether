/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.common.provider;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.util.Optional;

public interface IUserProvider {
    Optional<User> byId(long id);

    Optional<User> byId(String id);

    Optional<Member> byId(long id, long guildId);

    Optional<Member> byId(long id, Guild guild);
}
