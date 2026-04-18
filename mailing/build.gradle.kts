plugins {
    id("aether.java-conventions")
    id("aether.maven-publish-conventions")
}

description = "Implementation for mailing"

dependencies {
    api(libs.slf4j)
    api(libs.jda)
    api(libs.angus)
    api(libs.guava)
    api(libs.commons.validator)
    api(libs.jspecify)
}