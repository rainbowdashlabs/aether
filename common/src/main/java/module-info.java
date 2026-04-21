module dev.chojo.aether.common {
    requires net.dv8tion.jda;
    requires java.desktop;
    requires dev.goldmensch.propane;
    requires io.github.kaktushose.jdac.core;
    requires org.jspecify;

    exports dev.chojo.aether.common.consumer;
    exports dev.chojo.aether.common.container;
    exports dev.chojo.aether.common.eventmanager;
    exports dev.chojo.aether.common.functions;
    exports dev.chojo.aether.common.provider;
    exports dev.chojo.aether.common.util;
}
