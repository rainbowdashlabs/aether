module dev.chojo.aether.serialization {
    requires transitive java.desktop;
    requires dev.chojo.aether.common;
    requires net.dv8tion.jda;
    requires tools.jackson.core;
    requires tools.jackson.databind;

    exports dev.chojo.aether.serialization.jackson;
    exports dev.chojo.aether.serialization.pojo;
    exports dev.chojo.aether.serialization.pojo.guild;
    exports dev.chojo.aether.serialization.pojo.guild.channel;
}
