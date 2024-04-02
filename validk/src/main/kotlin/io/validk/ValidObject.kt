@file:Suppress("UNCHECKED_CAST")

package io.validk

@Suppress("UNCHECKED_CAST")
interface ValidObject<T> {

    val validation: Validation<T>

    fun validate(): ValidationErrors? {
        return validation.validate(this as T)
    }

    fun <R> validate(block: ValidationCheck<T, R>.() -> Unit): R {
        return validation.validate(this as T, block)
    }
}
