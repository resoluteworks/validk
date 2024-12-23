package io.validk

import io.kotest.core.spec.style.StringSpec
import io.validk.constraints.email
import io.validk.constraints.minLength
import io.validk.constraints.notBlank
import io.validk.test.shouldBeFailure
import io.validk.test.shouldBeSuccess

class NullableTest : StringSpec({

    "validate if not null" {
        data class Person(val name: String, val email: String?)

        val validation = Validation {
            failFast(false)
            Person::name { notBlank() }
            Person::email ifNotNull {
                notBlank()
                email()
            }
        }

        validation.validate(Person("John Smith", null)).shouldBeSuccess()
        validation.validate(Person("John Smith", "")).shouldBeFailure(
            ValidationError("email", "Cannot be blank"),
            ValidationError("email", "Must be a valid email")
        )
        validation.validate(Person("John Smith", "aaa")).shouldBeFailure(
            ValidationError("email", "Must be a valid email")
        )
        validation.validate(Person("John Smith", "john.smith@test")).shouldBeFailure(
            ValidationError("email", "Must be a valid email")
        )
        validation.validate(Person("John Smith", "john.smith@test.com")).shouldBeSuccess()
    }

    "cannot be null" {
        data class Person(val name: String, val email: String?, val lastName: String?)

        val validation = Validation {
            Person::name { notBlank() }
            Person::email.notNull("email cannot be null") {
                email()
            }
            Person::lastName.notNull("lastName cannot be null")
        }
        validation.validate(Person("John Smith", null, null)).shouldBeFailure(
            ValidationError("email", "email cannot be null"),
            ValidationError("lastName", "lastName cannot be null")
        )
        validation.validate(Person("John Smith", "aaa", null)).shouldBeFailure(
            ValidationError("email", "Must be a valid email"),
            ValidationError("lastName", "lastName cannot be null")
        )
    }

    "notNullOrBlank" {
        data class Person(val name: String?)

        val validation = Validation {
            Person::name.notNullOrBlank("Please enter a name")
        }
        validation.validate(Person(null)).shouldBeFailure(ValidationError("name", "Please enter a name"))
        validation.validate(Person("")).shouldBeFailure(ValidationError("name", "Please enter a name"))
        validation.validate(Person("   ")).shouldBeFailure(ValidationError("name", "Please enter a name"))
        validation.validate(Person("   \t \n \t ")).shouldBeFailure(ValidationError("name", "Please enter a name"))
    }

    "notNullOrBlank block" {
        data class Person(val name: String?)

        val validation = Validation {
            Person::name.notNullOrBlank("Please enter a name") {
                minLength(10) message "Name too short"
            }
        }
        validation.validate(Person(null)).shouldBeFailure(ValidationError("name", "Please enter a name"))
        validation.validate(Person("")).shouldBeFailure(ValidationError("name", "Please enter a name"))
        validation.validate(Person("   ")).shouldBeFailure(ValidationError("name", "Please enter a name"))
        validation.validate(Person("   \t \n \t ")).shouldBeFailure(ValidationError("name", "Please enter a name"))
        validation.validate(Person("john")).shouldBeFailure(ValidationError("name", "Name too short"))
    }

    "cannot be null custom message" {
        data class Person(val name: String, val email: String?)

        val validation = Validation {
            Person::name { notBlank() }
            Person::email.notNull("Please enter an email") {
                email()
            }
        }
        validation.validate(Person("John Smith", null)).shouldBeFailure(
            ValidationError("email", "Please enter an email")
        )
        validation.validate(Person("John Smith", "aaa")).shouldBeFailure(
            ValidationError("email", "Must be a valid email")
        )
    }
})
