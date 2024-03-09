plugins {
    kotlin("jvm")
    id("jacoco")
    id("com.github.nbaztec.coveralls-jacoco")
}

repositories {
    mavenLocal()
    mavenCentral()
}

val validkVersion:String by project
group = "io.resoluteworks"
version = validkVersion

kotlin {
    jvmToolchain(17)
}

dependencies {
    val kotlinVersion: String by project

    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
}
