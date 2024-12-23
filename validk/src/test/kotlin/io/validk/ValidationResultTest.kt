package io.validk

import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ValidationResultTest : StringSpec({

    "map with arguments" {
        ValidationResult.Success("Hello").map(
            success = { value -> value },
            error = { value, errors -> "Error" }
        ) shouldBe "Hello"

        ValidationResult.Failure("", ValidationError("name", "Must not be blank")).map(
            success = { value -> value },
            error = { _, _ -> "Error" }
        ) shouldBe "Error"
    }

    "map with lambdas" {
        ValidationResult.Success("Hello").map(
            success = { value -> value },
            error = { _, _ -> "Error" }
        ) shouldBe "Hello"

        ValidationResult.Failure("", ValidationError("name", "Must not be blank")).map(
            success = { value -> value },
            error = { _, _ -> "Error" }
        ) shouldBe "Error"
    }

    "throw IllegalStateException when success handler is not set" {
        shouldThrowWithMessage<IllegalStateException>("Success handler missing") {
            ValidationResult.Success("Hello").map { error = { _, _ -> "Error" } }
        }
    }

    "throw IllegalStateException when error handler is not set" {
        shouldThrowWithMessage<IllegalStateException>("Error handler missing") {
            ValidationResult.Success("Hello").map { success = { value -> value } }
        }
    }
})
