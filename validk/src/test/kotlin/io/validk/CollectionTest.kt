package io.validk

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class CollectionTest : StringSpec({

    "empty, min/max size" {
        Validation<List<String>> { notEmpty() }.validate(emptyList()) shouldNotBe null
        Validation<List<String>> { notEmpty() }.validate(listOf("AA")) shouldBe null

        Validation<List<String>> { minSize(2) }.validate(listOf("AA")) shouldNotBe null
        Validation<List<String>> { minSize(2) }.validate(listOf("AA", "BB")) shouldBe null
        Validation<List<String>> { minSize(2) }.validate(listOf("AA", "BB", "C")) shouldBe null

        Validation<List<String>> { maxSize(2) }.validate(listOf("AA")) shouldBe null
        Validation<List<String>> { maxSize(2) }.validate(listOf("AA", "BB")) shouldBe null
        Validation<List<String>> { maxSize(2) }.validate(listOf("AA", "BB", "C")) shouldNotBe null
    }

    "nested collection" {
        data class Child(val childName: String)
        data class Parent(val name: String, val children: List<Child>)

        val validation = Validation<Parent> {
            Parent::name { notBlank() }
            Parent::children { notEmpty() message "children list cannot be empty" }
            Parent::children each {
                Child::childName { notBlank() }
            }
        }

        validation.validate(Parent("John Smith", emptyList())) shouldBe errors(ValidationError("children", "children list cannot be empty"))
        validation.validate(Parent("John Smith", listOf(Child("One"), Child("Two")))) shouldBe null
        validation.validate(Parent("John Smith", listOf(Child(""), Child("Two"), Child("")))) shouldBe errors(
            ValidationError("children[0].childName", "cannot be blank"),
            ValidationError("children[2].childName", "cannot be blank")
        )
    }
})
