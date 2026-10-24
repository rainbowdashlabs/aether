plugins {
    id("aether.java-conventions")
    id("aether.publishing-conventions")
}

description = "Implementation for supporter checks"

dependencies {
    api(project(":common"))
    api(libs.jdacommands)
    api(libs.jda)
    api(libs.jackson.core)
}
