package io.validk.constraints

import io.kotest.core.spec.style.StringSpec
import io.validk.Validation
import io.validk.ValidationError
import io.validk.test.shouldBeFailure
import io.validk.test.shouldBeSuccess

class StringConstraintsTest : StringSpec({

    "notEmpty" {
        Validation<String> { this.notEmpty() }.validate("abc").shouldBeSuccess()
        Validation<String> { this.notEmpty() }.validate("  ").shouldBeSuccess()
        Validation<String>("name") { this.notEmpty() }
            .validate("")
            .shouldBeFailure(
                ValidationError("name", "Cannot be empty")
            )
        Validation<String>("name") { this.notEmpty() message "Value not allowed" }
            .validate("")
            .shouldBeFailure(
                ValidationError("name", "Value not allowed")
            )
    }

    "notBlank" {
        Validation { notBlank() }.validate("abc").shouldBeSuccess()
        Validation { notBlank() }.validate("  abc ").shouldBeSuccess()

        Validation("lastName") { notBlank() }.validate("  ").shouldBeFailure(
            ValidationError("lastName", "Cannot be blank")
        )

        Validation("firstName") { notBlank() message "First name cannot be blank" }.validate("").shouldBeFailure(
            ValidationError("firstName", "First name cannot be blank")
        )
    }

    "minLength" {
        Validation { minLength(5) }.validate("abcde").shouldBeSuccess()
        Validation("name") { minLength(5) }.validate("abcd").shouldBeFailure(
            ValidationError("name", "Must be at least 5 characters long")
        )
        Validation("lastName") { minLength(5) message { "Too short: ${it.length}" } }.validate("a").shouldBeFailure(
            ValidationError("lastName", "Too short: 1")
        )
        Validation("name") { minLength(5) }.validate(" ").shouldBeFailure(
            ValidationError("name", "Must be at least 5 characters long")
        )

        Validation("name") { minLength(5) }.validate("").shouldBeFailure(
            ValidationError("name", "Must be at least 5 characters long")
        )
    }

    "maxLength" {
        Validation("name") { maxLength(5) }.validate("abcdef").shouldBeFailure(
            ValidationError("name", "Must be at most 5 characters long")
        )

        Validation { maxLength(5) }.validate("abcde").shouldBeSuccess()
        Validation { maxLength(5) }.validate("abcd").shouldBeSuccess()
        Validation { maxLength(5) }.validate("a").shouldBeSuccess()
        Validation { maxLength(5) }.validate(" ").shouldBeSuccess()
        Validation { maxLength(5) }.validate("").shouldBeSuccess()
    }

    "maxWords" {
        Validation { maxWords(3) }.validate("one two three").shouldBeSuccess()
        Validation { maxWords(3) }.validate("one two").shouldBeSuccess()
        Validation { maxWords(3) }.validate("one").shouldBeSuccess()
        Validation { maxWords(3) }.validate("").shouldBeSuccess()

        Validation("description") { maxWords(3) }.validate("one two three four").shouldBeFailure(
            ValidationError("description", "Must be at most 3 words")
        )
    }

    "enum string" {
        Validation { enum("PERSON", "COMPANY") }.validate("PERSON").shouldBeSuccess()
        Validation("type") { enum("PERSON", "COMPANY") }.validate("OTHER").shouldBeFailure(
            ValidationError("type", "Must be one of: PERSON, COMPANY")
        )
        Validation("type") { enum("PERSON", "COMPANY") }.validate("person").shouldBeFailure(
            ValidationError("type", "Must be one of: PERSON, COMPANY")
        )
    }

    "enum type" {
        Validation { enum<TestEnum>() }.validate("COMPANY").shouldBeSuccess()
        Validation("type") { enum<TestEnum>() }.validate("OTHER").shouldBeFailure(
            ValidationError("type", "Must be one of: PERSON, COMPANY")
        )
    }

    "matches regex" {
        Validation { matches("[a-zA-Z]+".toRegex()) }.validate("abcdef").shouldBeSuccess()
        Validation("name") { matches("[a-zA-Z]+".toRegex()) }.validate("abcdef ").shouldBeFailure(
            ValidationError("name", "Must match pattern [a-zA-Z]+")
        )
        Validation { matches("[a-zA-Z]+".toRegex()) }.validate("abc1ef").shouldBeFailure()
    }

    "matches pattern string" {
        Validation { matches("[a-zA-Z]+") }.validate("abcdef").shouldBeSuccess()
        Validation("name") { matches("[a-zA-Z]+") }.validate("abcdef ").shouldBeFailure(
            ValidationError("name", "Must match pattern [a-zA-Z]+")
        )

        Validation { matches("[a-zA-Z]+") }.validate("abc1ef").shouldBeFailure()
    }

    "email" {
        Validation("emailAddress") { email() }.validate("test").shouldBeFailure(
            ValidationError("emailAddress", "Must be a valid email")
        )
        Validation { email() }.validate("test@domain").shouldBeFailure(
            ValidationError("", "Must be a valid email")
        )
        Validation { email() }.validate("test@domain.com").shouldBeSuccess()

        Validation { email() }.validate("").shouldBeFailure(
            ValidationError("", "Must be a valid email")
        )
        Validation { email() }.validate("").shouldBeFailure(
            ValidationError("", "Must be a valid email")
        )
        Validation { email() }.validate("test").shouldBeFailure(
            ValidationError("", "Must be a valid email")
        )
    }
}) {
    private enum class TestEnum {
        PERSON,
        COMPANY
    }
}
