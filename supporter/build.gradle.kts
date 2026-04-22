plugins {
    id("aether.java-conventions")
    id("aether.maven-publish-conventions")
}

description = "Implementation for supporter checks"

dependencies {
    api(project(":common"))
    api(libs.jdacommands)
    api(libs.jda)
    api(libs.jackson.core)
}
