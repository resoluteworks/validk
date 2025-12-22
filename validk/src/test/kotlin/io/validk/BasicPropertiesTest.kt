package io.validk

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.validk.constraints.gte
import io.validk.constraints.matches
import io.validk.constraints.minLength
import io.validk.constraints.notBlank
import io.validk.test.shouldBeFailure

class BasicPropertiesTest : StringSpec({

    "one field failed, one error" {
        data class Person(val name: String, val age: Int)

        val validation = Validation {
            Person::name { notBlank() }
            Person::age { gte(18) message "Must be at least 18" }
        }
        val result = validation.validate(Person(name = "John Smith", age = 12))
        result.value shouldBe Person(name = "John Smith", age = 12)
        result.shouldBeFailure(ValidationError("age", "Must be at least 18"))
    }

    "one field failed, multiple errors" {
        data class Person(val name: String, val age: Int)
        Validation {
            failFast(false)
            Person::name {
                notBlank() message "Should not be blank"
                matches("[a-zA-Z]+ [a-zA-Z]+") message "Doesn't match expected pattern"
            }
            Person::age { gte(18) }
        }.validate(Person(name = "", age = 23)).shouldBeFailure(
            ValidationError("name", "Should not be blank"),
            ValidationError("name", "Doesn't match expected pattern")
        )
    }

    "fail fast" {
        data class Person(val name: String, val age: Int)
        Validation {
            Person::name {
                notBlank()
                matches("[a-zA-Z]+ [a-zA-Z]+")
            }
            Person::age { gte(18) }
        }.validate(Person(name = "", age = 23)).shouldBeFailure(
            ValidationError("name", "Cannot be blank")
        )

        Validation {
            Person::name {
                notBlank()
                matches("[a-zA-Z]+ [a-zA-Z]+")
                minLength(100)
            }
            Person::age { gte(18) }
        }.validate(Person(name = "test", age = 23)).shouldBeFailure(
            // Second error gets picked up, but not the 3rd
            ValidationError("name", "Must match pattern [a-zA-Z]+ [a-zA-Z]+")
        )

        Validation {
            Person::name {
                matches("[a-zA-Z]+ [a-zA-Z]+")
                notBlank()
            }
            Person::age { gte(18) }
        }.validate(Person(name = "", age = 23)).shouldBeFailure(
            ValidationError("name", "Must match pattern [a-zA-Z]+ [a-zA-Z]+")
        )
    }

    "one field failed, multiple errors, first error only" {
        data class Person(val name: String, val age: Int)
        Validation {
            Person::name {
                notBlank()
                matches("[a-zA-Z]+ [a-zA-Z]+")
            }
            Person::age { gte(18) }
        }.validate(Person(name = "", age = 23)).shouldBeFailure(ValidationError("name", "Cannot be blank"))
    }

    "adding validation for the same property appends to validations" {
        data class Person(val name: String, val age: Int)
        Validation {
            failFast(false)
            Person::name { notBlank() }
            Person::name { minLength(5) }
        }.validate(Person(name = "", age = 23)).shouldBeFailure(
            ValidationError("name", "Cannot be blank"),
            ValidationError("name", "Must be at least 5 characters")
        )
    }

    "adding multiple validations to the same property appends to validations" {
        data class Person(val name: String, val age: Int)
        Validation {
            failFast(false)
            Person::name {
                notBlank() message "Cannot be blank"
                minLength(5) message "Must be at least 5 characters long"
            }
        }.validate(Person(name = "", age = 23)).shouldBeFailure(
            ValidationError("name", "Cannot be blank"),
            ValidationError("name", "Must be at least 5 characters long")
        )
    }
})
