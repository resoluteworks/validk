package io.validk

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ValidationErrorsTest : StringSpec({

    "errorsByPath" {
        ValidationErrors(
            ValidationError("name", "Must not be blank"),
            ValidationError("age", "Must be at least 18"),
            ValidationError("addresses[2].postcode.value", "Must not be blank")
        ).errorsByPath shouldBe mapOf(
            "name" to listOf("Must not be blank"),
            "age" to listOf("Must be at least 18"),
            "addresses[2].postcode.value" to listOf("Must not be blank")
        )

        ValidationErrors(
            ValidationError("name", "Must not be blank"),
            ValidationError("name", "Must be at least 10 characters long"),
            ValidationError("addresses[2].postcode.value", "Must not be blank")
        ).errorsByPath shouldBe mapOf(
            "name" to listOf("Must not be blank", "Must be at least 10 characters long"),
            "addresses[2].postcode.value" to listOf("Must not be blank")
        )
    }

    "errors(propertyPath)" {
        ValidationErrors(
            ValidationError("name", "Must not be blank"),
            ValidationError("age", "Must be at least 18"),
            ValidationError("addresses[2].postcode.value", "Must not be blank")
        ).errors("name") shouldBe listOf("Must not be blank")

        ValidationErrors(
            ValidationError("name", "Must not be blank"),
            ValidationError("name", "Must be at least 10 characters long"),
            ValidationError("addresses[2].postcode.value", "Must not be blank")
        ).errors("name") shouldBe listOf("Must not be blank", "Must be at least 10 characters long")

        ValidationErrors(
            ValidationError("name", "Must not be blank"),
            ValidationError("name", "Must be at least 10 characters long")
        ).errors("age") shouldBe emptyList()
    }

    "error(propertyPath)" {
        ValidationErrors(
            ValidationError("name", "Must not be blank"),
            ValidationError("age", "Must be at least 18")
        ).error("name") shouldBe "Must not be blank"

        ValidationErrors(
            ValidationError("name", "Must not be blank"),
            ValidationError("name", "Must be at least 10 characters long")
        ).error("name") shouldBe "Must not be blank"

        ValidationErrors(
            ValidationError("name", "Must not be blank"),
            ValidationError("name", "Must be at least 10 characters long")
        ).error("age") shouldBe null
    }

    "failedProperties" {
        ValidationErrors(
            ValidationError("name", "Must not be blank"),
            ValidationError("age", "Must be at least 18")
        ).failedProperties shouldBe setOf("name", "age")

        ValidationErrors(
            ValidationError("name", "Must not be blank"),
            ValidationError("name", "Must be at least 10 characters long")
        ).failedProperties shouldBe setOf("name")
    }

    "hasErrors" {
        ValidationErrors(
            ValidationError("name", "Must not be blank"),
            ValidationError("age", "Must be at least 18")
        ).hasErrors("name") shouldBe true

        ValidationErrors(
            ValidationError("name", "Must not be blank"),
            ValidationError("name", "Must be at least 10 characters long")
        ).hasErrors("name") shouldBe true

        ValidationErrors(
            ValidationError("name", "Must not be blank"),
            ValidationError("name", "Must be at least 10 characters long")
        ).hasErrors("age") shouldBe false
    }
})
