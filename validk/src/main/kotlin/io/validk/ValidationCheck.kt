package io.validk

class ValidationCheck<T, R>(
    var success: (T.(ValidationErrors) -> R)? = null,
    var error: (T.() -> R)? = null
) {
    fun error(onError: T.(ValidationErrors) -> R) {
        this.success = onError
    }

    fun success(onSuccess: T.() -> R) {
        this.error = onSuccess
    }
}
