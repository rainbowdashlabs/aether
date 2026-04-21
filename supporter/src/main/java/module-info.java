module dev.chojo.aether.supporter {
    requires net.dv8tion.jda;
    requires tools.jackson.core;
    requires tools.jackson.databind;
    requires io.github.kaktushose.jdac.core;
    requires org.slf4j;
    requires org.jspecify;
    requires com.google.common;
    requires com.google.guice;
    requires org.apache.commons.collections4;
    requires org.jetbrains.annotations;

    exports dev.chojo.aether.supporter.access;
    exports dev.chojo.aether.supporter.configuration;
    exports dev.chojo.aether.supporter.configuration.modules;
    exports dev.chojo.aether.supporter.configuration.modules.dummy;
    exports dev.chojo.aether.supporter.configuration.modules.feature;
    exports dev.chojo.aether.supporter.configuration.modules.subscriptions;
    exports dev.chojo.aether.supporter.configuration.modules.subscriptions.platform;
    exports dev.chojo.aether.supporter.configuration.modules.subscriptions.platform.purchase;
    exports dev.chojo.aether.supporter.jackson;
    exports dev.chojo.aether.supporter.registry;
    exports dev.chojo.aether.supporter.service;
    exports dev.chojo.aether.supporter.service.context;
}
