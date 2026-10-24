module dev.chojo.aether.mailing {
    requires transitive dev.chojo.aether.common;
    requires transitive net.dv8tion.jda;
    requires transitive org.jspecify;
    requires org.slf4j;
    requires jakarta.mail;
    requires com.google.common;
    requires org.apache.commons.validator;
    requires org.eclipse.angus.mail;

    exports dev.chojo.aether.mailing;
    exports dev.chojo.aether.mailing.configuration;
    exports dev.chojo.aether.mailing.entities;
    exports dev.chojo.aether.mailing.service;
    exports dev.chojo.aether.mailing.util;
}
