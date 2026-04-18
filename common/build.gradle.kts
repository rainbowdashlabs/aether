plugins {
    id("aether.java-conventions")
    id("aether.maven-publish-conventions")
}

description = "Common implementation for all modules"

dependencies {
    api(libs.jda)
}
