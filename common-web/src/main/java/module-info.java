module dev.chojo.aether.commonweb {
    requires transitive io.javalin;
    requires com.fasterxml.jackson.annotation;

    exports dev.chojo.aether.commonweb.error;
    exports dev.chojo.aether.commonweb.util;
}
