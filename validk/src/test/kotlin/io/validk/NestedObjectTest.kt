package io.validk

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class NestedObjectTest : StringSpec({

    "nested object" {
        data class Address(val city: String, val postCode: String)
        data class Person(val name: String, val address: Address)

        val validation = Validation {
            Person::name { notBlank() }
            Person::address {
                Address::city { notBlank() }
                Address::postCode { minLength(4) }
            }
        }

        validation.validate(Person("", Address("London", "ABCD"))) shouldBe errors(ValidationError("name", "cannot be blank"))
        validation.validate(Person("", Address("", "ABCD"))) shouldBe errors(
            ValidationError("name", "cannot be blank"),
            ValidationError("address.city", "cannot be blank")
        )
        validation.validate(Person("", Address("", "ABC"))) shouldBe errors(
            ValidationError("name", "cannot be blank"),
            ValidationError("address.city", "cannot be blank"),
            ValidationError("address.postCode", "must be at least 4 characters")
        )

        validation.validate(Person("John Smith", Address("", "ABC"))) shouldBe errors(
            ValidationError("address.city", "cannot be blank"),
            ValidationError("address.postCode", "must be at least 4 characters")
        )

        validation.validate(Person("John Smith", Address("London", "ABCD"))) shouldBe null
    }

})
