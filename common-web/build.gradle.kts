plugins {
    id("aether.java-conventions")
    id("aether.publishing-conventions")
}

description = "Implementation for common web classes"

dependencies {
    api(libs.javalin)
    api(libs.jackson.annotations)

    testImplementation(libs.jackson.databind)
}
