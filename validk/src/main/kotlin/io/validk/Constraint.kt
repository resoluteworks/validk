package io.validk

class Constraint<T>(
    var errorMessage: String,
    val check: (T) -> Boolean
) {

    internal fun check(propertyPath: String, value: T): ValidationError? {
        return if (!check(value)) {
            ValidationError(propertyPath, errorMessage)
        } else {
            null
        }
    }
}
