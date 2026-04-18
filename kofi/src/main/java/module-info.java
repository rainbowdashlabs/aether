module dev.chojo.aether.kofi {
    requires dev.chojo.aether.common;
    requires dev.chojo.aether.mailing;
    requires dev.chojo.aether.supporter;
    requires tools.jackson.databind;
    requires org.slf4j;
    requires net.dv8tion.jda;

    exports dev.chojo.aether.kofi.configuration;
    exports dev.chojo.aether.kofi.exception;
    exports dev.chojo.aether.kofi.pojo;
    exports dev.chojo.aether.kofi.service;
}
