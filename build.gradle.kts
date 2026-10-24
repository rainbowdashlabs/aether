plugins {
    alias(libs.plugins.aggregate.javadoc)
    id("aether.java-conventions")
    id("aether.publishing-conventions")
}

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
