plugins {
    id("aether.java-conventions")
    id("aether.publishing-conventions")
}

description = "Common implementation for all modules"

dependencies {
    api(libs.jda)
    api(libs.jdacommands)
    api(libs.jspecify)
}
