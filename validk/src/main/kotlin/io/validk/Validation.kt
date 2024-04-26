package io.validk

import kotlin.reflect.KProperty1

internal typealias DynamicValidation<T> = Validation<T>.(T) -> Unit

class Validation<T>(
    private val propertyPath: String,
    private val validatesCollectionElements: Boolean = false,
    private var failFast: Boolean = true,
    private val nullMessage: String = "is required"
) {
    private val constraints = mutableListOf<Constraint<T>>()
    private val childValidations = mutableMapOf<KProperty1<T, Any?>, MutableList<Validation<Any>>>()
    private val dynamicValidations = mutableListOf<DynamicValidation<T>>()

    fun failFast(failFast: Boolean = true) {
        this.failFast = failFast
    }

    fun addConstraint(errorMessage: String, test: (T) -> Boolean): Constraint<T> {
        val constraint = Constraint(errorMessage, test)
        constraints.add(constraint)
        return constraint
    }

    infix fun <R> Constraint<R>.message(errorMessage: String): Constraint<R> {
        this.errorMessage = errorMessage
        return this
    }

    operator fun <R> KProperty1<T, R>.invoke(init: Validation<R>.() -> Unit) {
        addChildValidation(this, init)
    }

    fun <R> KProperty1<T, R>.whenIs(value: R, block: Validation<T>.(T) -> Unit) {
        val property = this
        dynamicValidations.add {
            if (property.get(it) == value) {
                block(it)
            }
        }
    }

    fun <E> KProperty1<T, Collection<E>>.whenContains(value: E, block: Validation<T>.(T) -> Unit) {
        val property = this
        dynamicValidations.add {
            if (property.get(it).contains(value)) {
                block(it)
            }
        }
    }

    infix fun <R> KProperty1<T, R?>.ifNotNull(block: Validation<R>.() -> Unit) {
        val property = this
        dynamicValidations.add {
            if (property.get(it) != null) {
                addChildValidation(property as KProperty1<T, R>, block)
            }
        }
    }

    fun <R> KProperty1<T, R?>.notNull(errorMessage: String, block: Validation<R>.() -> Unit = {}) {
        val property = this
        addChildValidation<R>(property, {}, nullMessage = errorMessage).apply {
            property.ifNotNull(block)
        }
    }

    fun KProperty1<T, String?>.notNullOrBlank(errorMessage: String, block: (Validation<String>.() -> Unit)? = null) {
        notNull(errorMessage) {
            notBlank() message errorMessage
            block?.let { block(this) }
        }
    }

    infix fun <E, R : Collection<E>> KProperty1<T, R>.each(block: Validation<E>.() -> Unit) {
        addChildValidation(this, block, checksCollectionElements = true)
    }

    private fun <E> addChildValidation(
        property: KProperty1<T, Any?>,
        init: Validation<E>.() -> Unit,
        checksCollectionElements: Boolean = false,
        nullMessage: String = this.nullMessage
    ): Validation<E> {
        val validation = Validation<E>(propertyPath.addProperty(property.name), checksCollectionElements, failFast, nullMessage)
        init(validation)
        childValidations.putIfAbsent(property, mutableListOf())
        childValidations[property]!!.add(validation as Validation<Any>)
        return validation
    }

    fun withValue(block: Validation<T>.(T) -> Unit) {
        dynamicValidations.add(block)
    }

    fun <R> validate(value: T, block: ValidationCheck<T, R>.() -> Unit): R {
        val builder = ValidationCheck<T, R>()
        block(builder)
        val errors = validate(value)
        return if (errors != null) {
            builder.error!!(value, errors)
        } else {
            builder.success!!(value)
        }
    }

    fun validate(value: T?): ValidationErrors? {
        if (value == null) {
            // Technically, we would not have a Validation<T> unless:
            // A) val pojo: T is non-nullable (and we added validations against it)
            // or
            // B) val pojo: T? is nullable, but we added a Validation for it (via notNull or ifNotNull)
            //
            // In either case, this value should not be null and if it is, then we should fail the validation for it now
            // as none of the nested logic/constraints can be applied anyway
            return ValidationErrors(propertyPath, nullMessage)
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
            val dynamicValidation = Validation<T>(propertyPath = propertyPath, failFast = failFast)
            dynamicValidations.forEach { valueValidation ->
                valueValidation(dynamicValidation, value)
            }
            dynamicValidation.validate(value)?.let { errors.addAll(it.validationErrors) }
        }

        childValidations.forEach { (property, validations) ->
            val propertyValue = property.get(value)
            validations.forEach { validation ->
                if (validation.validatesCollectionElements) {
                    (propertyValue as Collection<*>).forEachIndexed { index, element ->
                        validation.validate(element!!)?.let { errors.addAll(it.validationErrors.map { it.indexed(property.name, index) }) }
                    }
                } else {
                    validation.validate(propertyValue)?.let { errors.addAll(it.validationErrors) }
                }
            }
        }

        return if (errors.isNotEmpty()) {
            ValidationErrors(errors)
        } else {
            null
        }
    }

    companion object {
        operator fun <T> invoke(init: Validation<T>.() -> Unit): Validation<T> {
            val validation = Validation<T>(propertyPath = "")
            return validation.apply(init)
        }
    }
}

private fun String.addProperty(property: String): String {
    return if (this.isBlank()) {
        property
    } else {
        "${this}.${property}"
    }
}
