pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://eldonexus.de/repository/maven-public/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "aether"

include("common")
include("common-web")
include("discord-oauth")
include("kofi")
include("mailing")
include("serialization")
include("supporter")
