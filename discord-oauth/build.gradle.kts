plugins {
    id("aether.java-conventions")
    id("aether.maven-publish-conventions")
}

description = "Implementation for discord oauth integration"

dependencies {
    api(libs.jackson.databind)
    api(libs.jackson.annotation)
    api(libs.guava)
    api(libs.javalin.core)
    api(libs.apache.httpclient)
    api(project(":common-web"))
}