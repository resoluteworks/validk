package io.validk

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ValidationCheckTest : StringSpec({

    "validation check" {
        data class Person(
            val name: String,
            val age: Int,
        )

        val validation = Validation {
            Person::name { notBlank() }
            Person::age { min(18) }
        }

        validation.validate(Person(name = "John Smith", age = 12)) {
            error { person, errors -> false }
            success { true }
        } shouldBe false

        validation.validate(Person(name = "John Smith", age = 23)) {
            error { person, errors -> false }
            success { true }
        } shouldBe true
    }

})