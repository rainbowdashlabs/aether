plugins {
    id("aether.java-conventions")
    id("aether.publishing-conventions")
}

description = "Implementation for kofi integration"

dependencies {
    api(project(":supporter"))
    api(project(":mailing"))
    api(project(":common"))
    api(libs.jackson.annotations)
    implementation(libs.slf4j.api)
}
