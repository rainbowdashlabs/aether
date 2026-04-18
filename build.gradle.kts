plugins {
    id("io.freefair.aggregate-javadoc") version("9.0.0")
    id("aether.java-conventions")
    id("aether.maven-publish-conventions")
}

group = "dev.chojo.aether"
version = "1.0.0"
description = "A library for discord bots based on jda-commands"

dependencies {
    api(project(":supporter"))
    api(project(":mailing"))
    api(project(":kofi"))
    api(project(":common"))
    javadoc(project(":supporter"))
    javadoc(project(":mailing"))
    javadoc(project(":kofi"))
    javadoc(project(":common"))
}