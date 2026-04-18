plugins {
    id("aether.java-conventions")
    id("aether.maven-publish-conventions")
}

description = "Implementation for common web classes"

dependencies {
    api(libs.javalin.core)
}
