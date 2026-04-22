plugins {
    id("io.freefair.aggregate-javadoc") version("9.0.0")
    id("aether.java-conventions")
    id("aether.maven-publish-conventions")
}

group = "dev.chojo.aether"
version = "1.2.4"
description = "A library for discord bots based on jda-commands"

dependencies {
    api(project(":common-web"))
    api(project(":common"))
    api(project(":discord-oauth"))
    api(project(":kofi"))
    api(project(":mailing"))
    api(project(":serialization"))
    api(project(":supporter"))
    javadoc(project(":common-web"))
    javadoc(project(":common"))
    javadoc(project(":discord-oauth"))
    javadoc(project(":kofi"))
    javadoc(project(":mailing"))
    javadoc(project(":serialization"))
    javadoc(project(":supporter"))
}
