plugins {
    id("aether.java-conventions")
    id("aether.publishing-conventions")
}

description = "Implementation for serialization of discord objects"

dependencies {
    api(libs.jackson.databind)
    api(libs.jackson.annotations)
    api(libs.jda)
    api(project(":common"))
}