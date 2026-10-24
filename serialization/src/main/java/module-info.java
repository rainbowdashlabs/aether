module dev.chojo.aether.serialization {
    requires transitive java.desktop;
    requires dev.chojo.aether.common;
    requires net.dv8tion.jda;
    requires com.fasterxml.jackson.annotation;
    requires tools.jackson.core;
    requires tools.jackson.databind;

    exports dev.chojo.aether.serialization.jackson;
    exports dev.chojo.aether.serialization.pojo;
    exports dev.chojo.aether.serialization.pojo.guild;
    exports dev.chojo.aether.serialization.pojo.guild.channel;

    opens dev.chojo.aether.serialization.pojo to
            tools.jackson.databind;
    opens dev.chojo.aether.serialization.pojo.guild to
            tools.jackson.databind;
    opens dev.chojo.aether.serialization.pojo.guild.channel to
            tools.jackson.databind;
}
