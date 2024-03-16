package io.validk

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class CollectionTest : StringSpec({

    "empty, min/max size" {
        validator<List<String>> { notEmpty() }.validate(emptyList()) shouldNotBe null
        validator<List<String>> { notEmpty() }.validate(listOf("AA")) shouldBe null

        validator<List<String>> { minSize(2) }.validate(listOf("AA")) shouldNotBe null
        validator<List<String>> { minSize(2) }.validate(listOf("AA", "BB")) shouldBe null
        validator<List<String>> { minSize(2) }.validate(listOf("AA", "BB", "C")) shouldBe null

        validator<List<String>> { maxSize(2) }.validate(listOf("AA")) shouldBe null
        validator<List<String>> { maxSize(2) }.validate(listOf("AA", "BB")) shouldBe null
        validator<List<String>> { maxSize(2) }.validate(listOf("AA", "BB", "C")) shouldNotBe null
    }
})
