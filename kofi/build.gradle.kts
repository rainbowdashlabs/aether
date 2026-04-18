plugins {
    id("aether.java-conventions")
    id("aether.maven-publish-conventions")
}

description = "Implementation for kofi integration"

dependencies {
    api(project(":supporter"))
    api(project(":mailing"))
    api(project(":common"))
    api(libs.jackson.databind)
    api(libs.slf4j)

}
