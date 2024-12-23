package io.validk.constraints

import io.kotest.core.spec.style.StringSpec
import io.validk.Validation
import io.validk.test.shouldBeFailure
import io.validk.test.shouldBeSuccess

class CollectionConstraintsTest : StringSpec({

    "notEmpty" {
        Validation<List<String>> { notEmpty() }.validate(emptyList()).shouldBeFailure()
        Validation<List<String>> { notEmpty() }.validate(listOf("AA")).shouldBeSuccess()
    }

    "minSize" {
        Validation<List<String>> { minSize(2) }.validate(listOf("AA")).shouldBeFailure()
        Validation<List<String>> { minSize(2) }.validate(listOf("AA", "BB")).shouldBeSuccess()
        Validation<List<String>> { minSize(2) }.validate(listOf("AA", "BB", "C")).shouldBeSuccess()
    }

    "maxSize" {
        Validation<List<String>> { maxSize(2) }.validate(listOf("AA")).shouldBeSuccess()
        Validation<List<String>> { maxSize(2) }.validate(listOf("AA", "BB")).shouldBeSuccess()
        Validation<List<String>> { maxSize(2) }.validate(listOf("AA", "BB", "C")).shouldBeFailure()
    }
})
