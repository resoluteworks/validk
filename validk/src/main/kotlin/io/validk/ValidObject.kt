@file:Suppress("UNCHECKED_CAST")

package io.validk

@Suppress("UNCHECKED_CAST")
interface ValidObject<Value> {

    val validation: Validation<Value>

    fun validate(): ValidationResult<Value> = validation.validate(this as Value) as ValidationResult<Value>
}
