plugins {
    id("aether.java-conventions")
    id("aether.publishing-conventions")
}

description = "Implementation for discord oauth integration"

dependencies {
    api(libs.jackson.databind)
    api(libs.jackson.annotations)
    api(libs.guava)
    api(libs.javalin)
    api(libs.httpclient)
    api(project(":common-web"))
    implementation(libs.slf4j.api)
}