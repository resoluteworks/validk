package io.validk

import io.kotest.core.spec.style.StringSpec
import kotlin.reflect.KProperty1

class Validation2Test : StringSpec({

    "test" {
        data class Child(val childName: String, val names: List<String>, val age: Int? = null)
        data class Parent(val name: String, val child: Child, val attributes: Map<String, Any>)

        val validator = Validator<Parent> {
            if (value.name == "john") {
                Parent::child {
                    Child::age.notNull {
                        addConstraint("greater than zero") { it > 0 }
                    }

                    Child::age.ifNotNull {
                    }

                    Child::childName {
                        addConstraint("Name cannot be null") { false }
                    }

                    Child::names {
                        addConstraint("List should not be empty") { value.isNotEmpty() }
                        each {
                            addConstraint("Cannot contain *") { !value.contains("*") }
                        }
                    }
                }

                Parent::attributes {
                    addConstraint("empty") { !it.isEmpty() }
                }
            }
        }
        val value = Parent("john", Child("", listOf("aaa", "bbb", "c*")), emptyMap())
        val errors = validator.validate(value)

        println(errors)
    }
})

sealed class ChildValidationBuilder<Parent, Child> {

    class Object<Parent, Child>(val property: KProperty1<Parent, Child>, val builder: ValidationBuilder<Child>) :
        ChildValidationBuilder<Parent, Child>()

    class Collection<Parent, Child>(val builder: ValidationBuilder<Child>) :
        ChildValidationBuilder<Parent, Child>()
}

class ValidationContext<T>(
    private val propertyPath: String,
    val value: T
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

    fun <R> KProperty1<T, R?>.ifNotNull(childBuilder: ValidationBuilder<R>) {
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
                        val childContext = ValidationContext(propertyPath.addProperty(child.property.name), childValue)
                        (child.builder as ValidationBuilder<Any>)(childContext)
                        errors.addAll(childContext.validate())
                    }

                    is ChildValidationBuilder.Collection -> {
                        (value as Collection<Any>).forEachIndexed { index, element ->
                            val childContext: ValidationContext<Any> = ValidationContext(propertyPath + "[${index}]", element)
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

infix fun <T, C : Collection<T>> ValidationContext<C>.each(childBuilder: ValidationBuilder<T>) {
    children.add(ChildValidationBuilder.Collection(childBuilder))
}

class Validator<T>(private val buildValidation: ValidationBuilder<T>) {

    fun validate(value: T): ValidationErrors? {
        val context = ValidationContext("", value)
        buildValidation(context)
        val errors = context.validate()
        return if (errors.isEmpty()) {
            null
        } else {
            ValidationErrors(errors)
        }
    }
}

internal typealias ValidationBuilder <T> = ValidationContext<T>.() -> Unit

private fun String?.addProperty(property: String): String {
    return if (this.isNullOrBlank()) {
        property
    } else {
        "${this}.${property}"
    }
}

