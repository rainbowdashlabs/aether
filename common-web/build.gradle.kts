plugins {
    id("aether.java-conventions")
    id("aether.publishing-conventions")
}

description = "Implementation for common web classes"

dependencies {
    api(libs.javalin)
}
