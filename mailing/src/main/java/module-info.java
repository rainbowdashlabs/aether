module dev.chojo.aether.mailing {
    requires net.dv8tion.jda;
    requires org.slf4j;
    requires jakarta.mail;
    requires com.google.common;
    requires org.jspecify;
    requires org.apache.commons.validator;
    requires org.eclipse.angus.mail;
    requires java.rmi;
    requires dev.goldmensch.propane;
    requires dev.chojo.aether.common;

    exports dev.chojo.aether.mailing;
    exports dev.chojo.aether.mailing.configuration;
    exports dev.chojo.aether.mailing.entities;
    exports dev.chojo.aether.mailing.service;
    exports dev.chojo.aether.mailing.util;
}
