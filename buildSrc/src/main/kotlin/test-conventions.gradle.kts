plugins {
    id("common-conventions")
}

dependencies {
    val kotestVersion = providers.gradleProperty("kotestVersion").get()
    val mockkVersion = providers.gradleProperty("mockkVersion").get()

    testImplementation("io.kotest:kotest-assertions-core:${kotestVersion}")
    testImplementation("io.kotest:kotest-property:${kotestVersion}")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:${kotestVersion}")
    testImplementation("io.mockk:mockk:${mockkVersion}")
    testImplementation("ch.qos.logback:logback-classic:1.4.11")
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        showStandardStreams = true
    }
    finalizedBy("jacocoTestReport")
}

tasks.jacocoTestReport {
    reports {
        html.required = true
        xml.required = true
    }
}

coverallsJacoco {
    reportPath = "validk/build/reports/jacoco/test/jacocoTestReport.xml"
}
