plugins {
    id("common-conventions")
    id("test-conventions")
    id("publish-conventions")
}

description = "A validation framework for Kotlin JVM with a type-safe DSL for defining value-aware and conditional validation rules."

dependencies {
    testImplementation(project(":validk-test"))
}
