plugins {
    id("aether.java-conventions")
    id("aether.publishing-conventions")
}

description = "Implementation for mailing"

dependencies {
    api(project(":common"))
    api(libs.slf4j.api)
    api(libs.jda)
    api(libs.angus.mail)
    api(libs.guava)
    api(libs.commons.validator)
    api(libs.jspecify)
}