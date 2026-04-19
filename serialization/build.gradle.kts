plugins {
    id("aether.java-conventions")
    id("aether.maven-publish-conventions")
}

description = "Implementation for serialization of discord objects"

dependencies {
    api(libs.jackson.databind)
    api(libs.jackson.annotation)
    api(libs.jda)
    api(project(":common"))
}