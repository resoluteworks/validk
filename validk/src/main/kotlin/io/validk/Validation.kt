package io.validk

import io.validk.constraints.Constraint
import io.validk.constraints.notBlank
import kotlin.reflect.KProperty1

internal typealias DynamicValidation<Value> = Validation<Value>.(Value) -> Unit

class Validation<Value>(
    private val propertyPath: String,
    private val validatesCollectionElements: Boolean = false,
    private var failFast: Boolean = true,
    private val nullMessage: String = "is required"
) {
    private val constraints = mutableListOf<Constraint<Value>>()
    private val childValidations = mutableMapOf<KProperty1<Value, Any?>, MutableList<Validation<Any?>>>()
    private val dynamicValidations = mutableListOf<DynamicValidation<Value>>()

    fun failFast(failFast: Boolean) {
        this.failFast = failFast
    }

    fun addConstraint(errorMessage: String, predicate: (Value) -> Boolean): Constraint<Value> {
        val constraint = Constraint(errorMessage, predicate)
        constraints.add(constraint)
        return constraint
    }

    operator fun <PropertyType> KProperty1<Value, PropertyType>.invoke(init: Validation<PropertyType>.() -> Unit) {
        addChildValidation(this, init)
    }

    fun <PropertyType> KProperty1<Value, PropertyType>.whenIs(value: PropertyType, block: Validation<Value>.(Value) -> Unit) {
        val property = this
        dynamicValidations.add {
            if (property.get(it) == value) {
                block(it)
            }
        }
    }

    infix fun <PropertyType> KProperty1<Value, PropertyType?>.ifNotNull(block: Validation<PropertyType>.() -> Unit) {
        val property = this
        dynamicValidations.add {
            if (property.get(it) != null) {
                addChildValidation(property as KProperty1<Value, PropertyType>, block)
            }
        }
    }

    fun <PropertyType> KProperty1<Value, PropertyType?>.notNull(errorMessage: String, block: Validation<PropertyType>.() -> Unit = {}) {
        val property = this
        addChildValidation<PropertyType>(property, {}, nullMessage = errorMessage).apply {
            property.ifNotNull(block)
        }
    }

    fun KProperty1<Value, String?>.notNullOrBlank(errorMessage: String, block: (Validation<String>.() -> Unit)? = null) {
        notNull(errorMessage) {
            notBlank() message errorMessage
            block?.let { block(this) }
        }
    }

    infix fun <ElementType, CollectionType : Collection<ElementType>> KProperty1<Value, CollectionType>.each(
        block: Validation<ElementType>.() -> Unit
    ) {
        addChildValidation(this, block, checksCollectionElements = true)
    }

    private fun <Type> addChildValidation(
        property: KProperty1<Value, Any?>,
        init: Validation<Type>.() -> Unit,
        checksCollectionElements: Boolean = false,
        nullMessage: String = this.nullMessage
    ): Validation<Type> {
        val validation = Validation<Type>(propertyPath.appendToPropertyPath(property.name), checksCollectionElements, failFast, nullMessage)
        init(validation)
        childValidations.putIfAbsent(property, mutableListOf())
        childValidations[property]!!.add(validation as Validation<Any?>)
        return validation
    }

    fun withValue(block: Validation<Value>.(Value) -> Unit) {
        dynamicValidations.add(block)
    }

    fun validate(value: Value): ValidationResult<Value> {
        if (value == null) {
            // Technically, we would not have a Validation<T> unless:
            // A) val pojo: T is non-nullable (and we added validations against it)
            // or
            // B) val pojo: T? is nullable, but we added a Validation for it (via notNull or ifNotNull)
            //
            // In either case, this value should not be null and if it is, then we should fail the validation for it now
            // as none of the nested logic/constraints can be applied anyway
            return ValidationResult.Failure(value, propertyPath, nullMessage)
        }

        val errors = mutableListOf<ValidationError>()

        run breaking@{
            constraints.forEach { constraint ->
                val error = constraint.check(propertyPath, value)
                error?.let { errors.add(error) }
                if (errors.isNotEmpty() && failFast) return@breaking
            }
        }

        if (dynamicValidations.isNotEmpty()) {
            val dynamicValidation = Validation<Value>(propertyPath = propertyPath, failFast = failFast)
            dynamicValidations.forEach { valueValidation ->
                valueValidation(dynamicValidation, value)
            }
            dynamicValidation.validate(value).ifFailed { errors.addAll(it.errors) }
        }

        childValidations.forEach { (property, validations) ->
            val propertyValue = property.get(value)
            validations.forEach { validation ->
                if (validation.validatesCollectionElements) {
                    (propertyValue as Collection<*>).forEachIndexed { index, element ->
                        validation.validate(element!!).ifFailed { errors.addAll(it.errors.map { it.indexed(property.name, index) }) }
                    }
                } else {
                    validation.validate(propertyValue).ifFailed { errors.addAll(it.errors) }
                }
            }
        }

        return if (errors.isEmpty()) {
            ValidationResult.Success(value)
        } else {
            ValidationResult.Failure(value, errors)
        }
    }

    companion object {
        operator fun <Value> invoke(propertyPath: String = "", init: Validation<Value>.() -> Unit): Validation<Value> {
            val validation = Validation<Value>(propertyPath = propertyPath)
            return validation.apply(init)
        }
    }
}

private fun String.appendToPropertyPath(property: String): String = if (this.isBlank()) {
    property
} else {
    "${this}.${property}"
}
