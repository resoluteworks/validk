package io.validk

import kotlin.reflect.KProperty1

internal typealias ValidationBuilder <T> = Validation<T>.() -> Unit

internal sealed class ChildValidationBuilder<Parent, Child> {
    class Object<Parent, Child>(val property: KProperty1<Parent, Child>, val builder: ValidationBuilder<Child>) :
        ChildValidationBuilder<Parent, Child>()

    class Collection<Parent, Child>(val builder: ValidationBuilder<Child>) :
        ChildValidationBuilder<Parent, Child>()
}


infix fun <T, C : Collection<T>> Validation<C>.each(childBuilder: ValidationBuilder<T>) {
    children.add(ChildValidationBuilder.Collection(childBuilder))
}

class Validation<T>(
    private val propertyPath: String,
    val value: T,
    private val eager: Boolean
) {

    private val constraints = mutableListOf<Constraint<T>>()
    internal val children = mutableListOf<ChildValidationBuilder<T, *>>()

    fun addConstraint(errorMessage: String, test: (T) -> Boolean): Constraint<T> {
        val constraint = Constraint(errorMessage, test)
        constraints.add(constraint)
        return constraint
    }

    operator fun <R> KProperty1<T, R>.invoke(childBuilder: ValidationBuilder<R>) {
        children.add(ChildValidationBuilder.Object(this, childBuilder))
    }

    fun <R> KProperty1<T, R?>.notNull(message: String = "${this.name} cannot be null", childBuilder: ValidationBuilder<R>) {
        addConstraint(message) { it != null }
        if (this.get(value) != null) {
            children.add(ChildValidationBuilder.Object(this, childBuilder as ValidationBuilder<*>))
        }
    }

    infix fun <R> KProperty1<T, R?>.ifNotNull(childBuilder: ValidationBuilder<R>) {
        if (this.get(value) != null) {
            children.add(ChildValidationBuilder.Object(this, childBuilder as ValidationBuilder<*>))
        }
    }

    fun validate(): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()
        constraints.forEach {
            val error = it.check(propertyPath, value)
            error?.let { errors.add(error) }
        }
        if (value != null) {
            children.forEach { child ->
                when (child) {
                    is ChildValidationBuilder.Object -> {
                        val childValue = child.property.get(value)
                        // Technically this should never happen
                            ?: throw IllegalStateException("Child property ${child.property.name} is null but a null-check hasn't been configured")
                        val childContext = Validation(propertyPath.addProperty(child.property.name), childValue, eager)
                        (child.builder as ValidationBuilder<Any>)(childContext)
                        errors.addAll(childContext.validate())
                    }

                    is ChildValidationBuilder.Collection -> {
                        (value as Collection<Any>).forEachIndexed { index, element ->
                            val childContext: Validation<Any> = Validation(propertyPath + "[${index}]", element, eager)
                            (child.builder as ValidationBuilder<Any>)(childContext)
                            errors.addAll(childContext.validate())
                        }
                    }
                }
            }
        }

        return errors
    }
}

fun interface Validator<T> {
    fun validate(value: T): ValidationErrors?
}

fun <T> validator(eager: Boolean = false, buildValidation: ValidationBuilder<T>): Validator<T> {
    return Validator { value ->
        val context = Validation("", value, eager)
        buildValidation(context)
        val errors = context.validate()
        if (errors.isEmpty()) {
            null
        } else {
            ValidationErrors(errors)
        }
    }
}

fun String.addProperty(property: String): String {
    return if (this.isBlank()) {
        property
    } else {
        "${this}.${property}"
    }
}
