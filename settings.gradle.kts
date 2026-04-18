rootProject.name = "aether"

include("supporter")
include("kofi")
include("mailing")
include("common")

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        maven {
            name = "EldoNexus"
            url = uri("https://eldonexus.de/repository/maven-public/")
        }
    }
}
