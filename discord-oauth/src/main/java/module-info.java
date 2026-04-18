module dev.chojo.aether.discordoauth {
    requires dev.chojo.aether.commonweb;
    requires tools.jackson.databind;
    requires com.fasterxml.jackson.annotation;
    requires com.google.common;
    requires io.javalin;
    requires org.apache.httpcomponents.client5.httpclient5;
    requires java.net.http;
    requires org.apache.httpcomponents.core5.httpcore5;

    exports dev.chojo.aether.discordoauth.configuration;
    exports dev.chojo.aether.discordoauth.pojo;
    exports dev.chojo.aether.discordoauth.service;
}
