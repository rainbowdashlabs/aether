import gradle.kotlin.dsl.accessors._83a41c35569feeb28b615dcf8647de1d.api

plugins {
    id("io.freefair.aggregate-javadoc") version("9.0.0")
    id("sadu.java-conventions")
    id("sadu.maven-publish-conventions")
}

group = "dev.chojo.aether"
version = "1.0.0"
description = "A library for discord bots based on jda-commands"

dependencies {
    api(project(":supporter"))
    api(project(":mailing"))
    api(project(":kofi"))
    api(project(":common"))
}