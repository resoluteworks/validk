package io.validk.test

import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.types.shouldBeTypeOf
import io.validk.ValidationError
import io.validk.ValidationResult

fun ValidationResult<*>.shouldBeSuccess() {
    this.shouldBeTypeOf<ValidationResult.Success<*>>()
}

fun ValidationResult<*>.shouldBeFailure(vararg errors: ValidationError) {
    this.shouldBeTypeOf<ValidationResult.Failure<*>>()

    if (errors.isNotEmpty()) {
        this.validationErrors.errors shouldContainExactlyInAnyOrder errors.toList()
    }
}
