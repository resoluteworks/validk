package io.validk.constraints

import io.validk.ValidationError

class Constraint<T>(
    private val predicate: (T) -> Boolean,
    private var errorMessage: (T) -> String
) {

    constructor(errorMessage: String, predicate: (T) -> Boolean) : this(predicate, { errorMessage })

    infix fun message(message: String): Constraint<T> {
        errorMessage = { message }
        return this
    }

    infix fun message(message: (T) -> String): Constraint<T> {
        errorMessage = message
        return this
    }

    internal fun check(propertyPath: String, value: T): ValidationError? {
        val passes = predicate(value)
        return if (passes) {
            null
        } else {
            ValidationError(propertyPath, errorMessage(value))
        }
    }
}
