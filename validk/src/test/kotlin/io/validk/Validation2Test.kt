package io.validk

import io.kotest.core.spec.style.StringSpec

class Validation2Test : StringSpec({

    "test" {
        data class Child(val childName: String, val names: List<String>, val age: Int? = null)
        data class Parent(val name: String, val child: Child, val attributes: Map<String, Any>)

        val validator = validator<Parent> {
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



