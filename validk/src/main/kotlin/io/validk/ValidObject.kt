@file:Suppress("UNCHECKED_CAST")

package io.validk

@Suppress("UNCHECKED_CAST")
interface ValidObject<T> {

    val validation: Validation<T>

    fun validate(): ValidationErrors? {
        return validation.validate(this as T)
    }

    fun <R> validate(block: ValidationCheck<T, R>.() -> Unit): R {
        val builder = ValidationCheck<T, R>()
        block(builder)
        val errors = validate()
        return if (errors != null) {
            builder.onError!!(this as T, errors)
        } else {
            builder.onSuccess!!(this as T)
        }
    }
}

class ValidationCheck<T, R>(
    var onError: (T.(ValidationErrors) -> R)? = null,
    var onSuccess: (T.() -> R)? = null
) {
    fun error(onError: T.(ValidationErrors) -> R) {
        this.onError = onError
    }

    fun success(onSuccess: T.() -> R) {
        this.onSuccess = onSuccess
    }
}
