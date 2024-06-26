package io.validk

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class BasicPropertiesTest : StringSpec({

    "one field failed, one error" {
        val validation = Validation {
            Person::name { notBlank() }
            Person::age { min(18) }
        }
        validation.validate(Person(name = "John Smith", age = 12)) shouldBe ValidationErrors(ValidationError("age", "must be at least 18"))
    }

    "one field failed, multiple errors" {
        Validation {
            failFast(false)
            Person::name {
                notBlank()
                matches("[a-zA-Z]+ [a-zA-Z]+")
            }
            Person::age { min(18) }
        }.validate(Person(name = "", age = 23)) shouldBe ValidationErrors(
            ValidationError("name", "cannot be blank"),
            ValidationError("name", "must match pattern [a-zA-Z]+ [a-zA-Z]+")
        )
    }

    "fail fast" {
        Validation {
            Person::name {
                notBlank()
                matches("[a-zA-Z]+ [a-zA-Z]+")
            }
            Person::age { min(18) }
        }.validate(Person(name = "", age = 23)) shouldBe ValidationErrors(
            ValidationError("name", "cannot be blank")
        )

        Validation {
            Person::name {
                notBlank()
                matches("[a-zA-Z]+ [a-zA-Z]+")
                minLength(100)
            }
            Person::age { min(18) }
        }.validate(Person(name = "test", age = 23)) shouldBe ValidationErrors(
            // Second error gets picked up, but not the 3rd
            ValidationError("name", "must match pattern [a-zA-Z]+ [a-zA-Z]+")
        )

        Validation {
            Person::name {
                matches("[a-zA-Z]+ [a-zA-Z]+")
                notBlank()
            }
            Person::age { min(18) }
        }.validate(Person(name = "", age = 23)) shouldBe ValidationErrors(
            ValidationError("name", "must match pattern [a-zA-Z]+ [a-zA-Z]+")
        )
    }

    "one field failed, multiple errors, first error only" {
        Validation {
            Person::name {
                notBlank()
                matches("[a-zA-Z]+ [a-zA-Z]+")
            }
            Person::age { min(18) }
        }.validate(Person(name = "", age = 23))!!.validationErrors shouldBe listOf(ValidationError("name", "cannot be blank"))
    }

    "custom error message" {
        Validation {
            failFast(false)
            Person::name {
                notBlank() message "Really now?"
                matches("[a-zA-Z]+ [a-zA-Z]+") message "Characters only please"
            }
            Person::age { min(18) }
        }.validate(Person(name = "", age = 23)) shouldBe ValidationErrors(
            ValidationError("name", "Really now?"),
            ValidationError("name", "Characters only please")
        )
    }
}) {

    private data class Person(
        val name: String,
        val age: Int,
    )
}

