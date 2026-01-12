plugins {
    kotlin("jvm")
    id("jacoco")
    id("com.github.nbaztec.coveralls-jacoco")
    id("org.jetbrains.dokka")
}

repositories {
    mavenLocal()
    mavenCentral()
}

val validkVersion: String by project
group = "works.resolute"
version = validkVersion

kotlin {
    jvmToolchain(21)
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.dokkaHtml {
    outputDirectory.set(layout.buildDirectory.dir("../../docs/dokka/${project.name}"))
}
