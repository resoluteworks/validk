plugins {
    id("common-conventions")
    id("test-conventions")
    id("publish-conventions")
}

description = "Kotest matchers for asserting on validk validation results and errors in tests."

dependencies {
    val kotestVersion = providers.gradleProperty("kotestVersion").get()

    implementation(project(":validk"))

    implementation("io.kotest:kotest-assertions-core:${kotestVersion}")
    implementation("io.kotest:kotest-property:${kotestVersion}")
    implementation("io.kotest:kotest-runner-junit5-jvm:${kotestVersion}")
}
