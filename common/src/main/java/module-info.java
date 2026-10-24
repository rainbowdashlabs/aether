module dev.chojo.aether.common {
    requires transitive net.dv8tion.jda;
    requires transitive java.desktop;
    requires transitive dev.goldmensch.propane;
    requires transitive io.github.kaktushose.jdac.core;
    requires transitive org.jspecify;
    requires org.slf4j;

    exports dev.chojo.aether.common.completion;
    exports dev.chojo.aether.common.consumer;
    exports dev.chojo.aether.common.container;
    exports dev.chojo.aether.common.eventmanager;
    exports dev.chojo.aether.common.functions;
    exports dev.chojo.aether.common.provider;
    exports dev.chojo.aether.common.registry;
    exports dev.chojo.aether.common.services;
    exports dev.chojo.aether.common.util;
}
