plugins {
    id("common-conventions")
    id("test-conventions")
    id("publish-conventions")
}

dependencies {
    val kotestVersion: String by project

    implementation(project(":validk"))

    implementation("io.kotest:kotest-assertions-core:${kotestVersion}")
    implementation("io.kotest:kotest-property:${kotestVersion}")
    implementation("io.kotest:kotest-runner-junit5-jvm:${kotestVersion}")
}
