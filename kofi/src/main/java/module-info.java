module dev.chojo.aether.kofi {
    requires transitive dev.chojo.aether.common;
    requires transitive dev.chojo.aether.mailing;
    requires transitive dev.chojo.aether.supporter;
    requires transitive net.dv8tion.jda;
    requires com.fasterxml.jackson.annotation;
    requires org.slf4j;

    exports dev.chojo.aether.kofi.configuration;
    exports dev.chojo.aether.kofi.exception;
    exports dev.chojo.aether.kofi.pojo;
    exports dev.chojo.aether.kofi.service;
}
