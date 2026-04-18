package dev.chojo.aether.supporter.access;

import net.dv8tion.jda.api.entities.Entitlement;

public enum SkuTarget {
    GUILD,USER;

    public static SkuTarget fromEntitlement(Entitlement entitlement) {
        return entitlement.getGuildId() == null ? USER : GUILD;
    }

}
