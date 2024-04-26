package io.validk

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ValidObjectTest : StringSpec({

    "valid object validation" {
        TestValidObject("", 12).validate() shouldBe ValidationErrors(
            ValidationError("name", "cannot be blank"),
            ValidationError("age", "must be at least 18")
        )
        TestValidObject("john smith ", 20).validate() shouldBe null
    }

    "validation check" {
        data class Result(val success: Boolean, val input: TestValidObject, val errors: ValidationErrors?)

        TestValidObject("", 12).validate {
            error { Result(false, this, it) }
            success { Result(true, it, null) }
        } shouldBe Result(
            false, TestValidObject("", 12), ValidationErrors(
                ValidationError("name", "cannot be blank"),
                ValidationError("age", "must be at least 18")
            )
        )

        TestValidObject("John smith", 20).validate {
            this.error { Result(false, this, it) }
            success { Result(true, it, null) }
        } shouldBe Result(true, TestValidObject("John smith", 20), null)
    }
}) {

    private data class TestValidObject(
        val name: String,
        val age: Int,
    ) : ValidObject<TestValidObject> {

        override val validation: Validation<TestValidObject> = Validation {
            TestValidObject::name {
                notBlank()
            }

            TestValidObject::age {
                min(18)
            }
        }
    }

}
