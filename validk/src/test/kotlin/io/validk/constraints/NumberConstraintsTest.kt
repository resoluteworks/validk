package io.validk.constraints

import io.kotest.core.spec.style.StringSpec
import io.validk.Validation
import io.validk.test.shouldBeFailure
import io.validk.test.shouldBeSuccess

class NumberConstraintsTest : StringSpec({

    "gte" {
        Validation<Int> { gte(3) }.validate(2).shouldBeFailure()
        Validation<Int> { gte(3) }.validate(3).shouldBeSuccess()
        Validation<Int> { gte(3) }.validate(100).shouldBeSuccess()

        Validation<Short> { gte(3) }.validate(2).shouldBeFailure()
        Validation<Short> { gte(3) }.validate(3).shouldBeSuccess()
        Validation<Short> { gte(3) }.validate(100).shouldBeSuccess()

        Validation<Double> { gte(3.0) }.validate(2.99999).shouldBeFailure()
        Validation<Double> { gte(3.0) }.validate(3.0).shouldBeSuccess()
        Validation<Double> { gte(3.0) }.validate(100.0).shouldBeSuccess()

        Validation<Float> { gte(3.0f) }.validate(2.99999f).shouldBeFailure()
        Validation<Float> { gte(3.0f) }.validate(3.0f).shouldBeSuccess()
        Validation<Float> { gte(3.0f) }.validate(100.0f).shouldBeSuccess()

        Validation<Long> { gte(3L) }.validate(2L).shouldBeFailure()
        Validation<Long> { gte(3L) }.validate(3L).shouldBeSuccess()
        Validation<Long> { gte(3L) }.validate(100L).shouldBeSuccess()
    }

    "gt" {
        Validation<Int> { gt(3) }.validate(2).shouldBeFailure()
        Validation<Int> { gt(3) }.validate(3).shouldBeFailure()
        Validation<Int> { gt(3) }.validate(4).shouldBeSuccess()

        Validation<Short> { gt(3) }.validate(2).shouldBeFailure()
        Validation<Short> { gt(3) }.validate(3).shouldBeFailure()
        Validation<Short> { gt(3) }.validate(4).shouldBeSuccess()

        Validation<Double> { gt(3.0) }.validate(2.99999).shouldBeFailure()
        Validation<Double> { gt(3.0) }.validate(3.0).shouldBeFailure()
        Validation<Double> { gt(3.0) }.validate(3.000001).shouldBeSuccess()

        Validation<Float> { gt(3.0f) }.validate(2.99999f).shouldBeFailure()
        Validation<Float> { gt(3.0f) }.validate(3.0f).shouldBeFailure()
        Validation<Float> { gt(3.0f) }.validate(3.000001f).shouldBeSuccess()

        Validation<Long> { gt(3L) }.validate(2L).shouldBeFailure()
        Validation<Long> { gt(3L) }.validate(3L).shouldBeFailure()
        Validation<Long> { gt(3L) }.validate(4).shouldBeSuccess()
    }

    "lte" {
        Validation<Int> { lte(3) }.validate(2).shouldBeSuccess()
        Validation<Int> { lte(3) }.validate(3).shouldBeSuccess()
        Validation<Int> { lte(3) }.validate(4).shouldBeFailure()

        Validation<Short> { lte(3) }.validate(2).shouldBeSuccess()
        Validation<Short> { lte(3) }.validate(3).shouldBeSuccess()
        Validation<Short> { lte(3) }.validate(4).shouldBeFailure()

        Validation<Double> { lte(3.0) }.validate(2.99999).shouldBeSuccess()
        Validation<Double> { lte(3.0) }.validate(3.0).shouldBeSuccess()
        Validation<Double> { lte(3.0) }.validate(3.00001).shouldBeFailure()

        Validation<Float> { lte(3.0f) }.validate(2.99999f).shouldBeSuccess()
        Validation<Float> { lte(3.0f) }.validate(3.0f).shouldBeSuccess()
        Validation<Float> { lte(3.0f) }.validate(3.00001f).shouldBeFailure()

        Validation<Long> { lte(3L) }.validate(2L).shouldBeSuccess()
        Validation<Long> { lte(3L) }.validate(3L).shouldBeSuccess()
        Validation<Long> { lte(3L) }.validate(4L).shouldBeFailure()
    }

    "lt" {
        Validation<Int> { lt(3) }.validate(2).shouldBeSuccess()
        Validation<Int> { lt(3) }.validate(3).shouldBeFailure()
        Validation<Int> { lt(3) }.validate(4).shouldBeFailure()

        Validation<Short> { lt(3) }.validate(2).shouldBeSuccess()
        Validation<Short> { lt(3) }.validate(3).shouldBeFailure()
        Validation<Short> { lt(3) }.validate(4).shouldBeFailure()

        Validation<Double> { lt(3.0) }.validate(2.99999).shouldBeSuccess()
        Validation<Double> { lt(3.0) }.validate(3.0).shouldBeFailure()
        Validation<Double> { lt(3.0) }.validate(3.00001).shouldBeFailure()

        Validation<Float> { lt(3.0f) }.validate(2.99999f).shouldBeSuccess()
        Validation<Float> { lt(3.0f) }.validate(3.0f).shouldBeFailure()
        Validation<Float> { lt(3.0f) }.validate(3.00001f).shouldBeFailure()

        Validation<Long> { lt(3L) }.validate(2L).shouldBeSuccess()
        Validation<Long> { lt(3L) }.validate(3L).shouldBeFailure()
        Validation<Long> { lt(3L) }.validate(4L).shouldBeFailure()
    }

    "between" {
        Validation<Int> { between(3..5) }.validate(2).shouldBeFailure()
        Validation<Int> { between(3..5) }.validate(3).shouldBeSuccess()
        Validation<Int> { between(3..5) }.validate(4).shouldBeSuccess()
        Validation<Int> { between(3..5) }.validate(5).shouldBeSuccess()
        Validation<Int> { between(3..5) }.validate(6).shouldBeFailure()

        Validation<Short> { between(3..5) }.validate(2).shouldBeFailure()
        Validation<Short> { between(3..5) }.validate(3).shouldBeSuccess()
        Validation<Short> { between(3..5) }.validate(4).shouldBeSuccess()
        Validation<Short> { between(3..5) }.validate(5).shouldBeSuccess()
        Validation<Short> { between(3..5) }.validate(6).shouldBeFailure()

        Validation<Long> { between(3L..5L) }.validate(2L).shouldBeFailure()
        Validation<Long> { between(3L..5L) }.validate(3L).shouldBeSuccess()
        Validation<Long> { between(3L..5L) }.validate(4L).shouldBeSuccess()
        Validation<Long> { between(3L..5L) }.validate(5L).shouldBeSuccess()
        Validation<Long> { between(3L..5L) }.validate(6L).shouldBeFailure()
    }
})
