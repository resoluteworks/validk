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

jacoco {
    // The org.jacoco.core jar on the buildSrc classpath is the single JaCoCo pin; its VERSION carries a
    // build timestamp (0.8.15.2026...) that the published agent and ant artifacts do not.
    toolVersion = org.jacoco.core.JaCoCo.VERSION.substringBeforeLast(".")
}

dokka {
    dokkaPublications.html {
        outputDirectory.set(layout.projectDirectory.dir("../docs/dokka/${project.name}"))
    }
}
