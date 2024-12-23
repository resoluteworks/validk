package io.validk

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.validk.constraints.gte
import io.validk.constraints.notBlank
import io.validk.test.shouldBeFailure
import io.validk.test.shouldBeSuccess

class ValidObjectTest : StringSpec({

    "valid object validation" {
        TestValidObject("", 12).validate().shouldBeFailure(
            ValidationError("name", "Cannot be blank"),
            ValidationError("age", "Must be at least 18")
        )
        TestValidObject("john smith ", 20).validate().shouldBeSuccess()
    }

    "validation check" {
        data class Result(val success: Boolean, val input: TestValidObject, val errors: ValidationErrors?)

        TestValidObject("", 12).validate().map {
            success { Result(true, it, null) }
            error { obj, errors -> Result(false, obj, errors) }
        } shouldBe Result(
            false, TestValidObject("", 12),
            ValidationErrors(
                ValidationError("name", "Cannot be blank"),
                ValidationError("age", "Must be at least 18")
            )
        )

        TestValidObject("John smith", 20).validate().map {
            success { Result(true, it, null) }
            error { obj, errors -> Result(false, obj, errors) }
        } shouldBe Result(true, TestValidObject("John smith", 20), null)
    }
}) {

    private data class TestValidObject(
        val name: String,
        val age: Int
    ) : ValidObject<TestValidObject> {

        override val validation: Validation<TestValidObject> = Validation {
            TestValidObject::name { notBlank() }
            TestValidObject::age { gte(18) message "Must be at least 18" }
        }
    }
}
