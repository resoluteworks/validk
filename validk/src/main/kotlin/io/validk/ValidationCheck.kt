package io.validk

class ValidationCheck<T, R>(
    var error: ((T, ValidationErrors) -> R)? = null,
    var success: ((T) -> R)? = null
) {
    fun error(onError: (T, ValidationErrors) -> R) {
        this.error = onError
    }

    fun success(onSuccess: (T) -> R) {
        this.success = onSuccess
    }
}
