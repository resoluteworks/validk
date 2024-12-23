package io.validk

import io.kotest.core.spec.style.StringSpec
import io.validk.constraints.email
import io.validk.constraints.gte
import io.validk.constraints.minLength
import io.validk.constraints.notBlank

class Examples : StringSpec({

    "Quick start" {
        data class Employee(val name: String, val email: String?)
        data class Organisation(val name: String, val employees: List<Employee>)

        val organisationValidation = Validation {
            // Organisation name should be at least 5 characters long
            Organisation::name { minLength(5) }

            Organisation::employees each {
                // Each employee should have a name that is at least 10 characters long.
                Employee::name { minLength(10) }

                // An employee can have an email address, but it's not required.
                // When present, it should be a valid email.
                Employee::email ifNotNull { email() }
            }
        }

        val org = Organisation(
            name = "ACME",
            employees = listOf(Employee("John", "john@test.com"), Employee("Hannah Johnson", "hanna"))
        )
        val result: ValidationResult<Organisation> = organisationValidation.validate(org)
        when (result) {
            is ValidationResult.Success -> println("Validation success")
            is ValidationResult.Failure -> result.allErrors.forEach { println(it) }
        }
    }

    "Error messages" {
        data class Employee(val email: String)

        val employeeValidation = Validation {
            // Dynamic error message
            Employee::email {
                email() message { value -> "Invalid email address: $value" }
            }

            // Static error message
            Employee::email {
                email() message "This emails address is invalid"
            }
        }
    }

    "Custom constraints" {
        data class Employee(val name: String)

        val employeeValidation = Validation {
            Employee::name {
                addConstraint("Must start with uppercase letter") {
                    it.first().isUpperCase() == true
                }
            }
        }
    }

    "Dynamic validation - withValue" {
        data class Entity(
            val type: String,
            val registeredOffice: String,
            val proofOfId: String
        )

        Validation<Entity> {
            withValue { entity ->
                when (entity.type) {
                    "PERSON" -> Entity::proofOfId { minLength(10) }
                    "COMPANY" -> Entity::registeredOffice { minLength(5) }
                }
            }
        }
    }

    "Dynamic validation - whenIs" {
        Validation {
            Entity::entityType.whenIs("PERSON") {
                Entity::proofOfId { minLength(10) }
            }

            Entity::entityType.whenIs("COMPANY") {
                Entity::registeredOffice { minLength(5) }
            }
        }
    }

    "Convert validation result" {
        data class Person(val name: String, val age: Int)
        data class Response(val status: HttpStatus, val model: Person, val errors: ValidationErrors? = null)

        val validation = Validation {
            Person::name { notBlank() }
            Person::age { gte(18) }
        }

        val personForm = Person(name = "John Smith", age = 12)
        val httpResponse = validation.validate(personForm).map {
            success { person ->
                Response(HttpStatus.OK, person)
            }
            error { person, errors ->
                Response(HttpStatus.BAD_REQUEST, person, errors)
            }
        }
    }

    "Valid object" {
        data class Person(val name: String, val email: String) : ValidObject<Person> {
            override val validation: Validation<Person> = Validation {
                Person::name { minLength(10) }
                Person::email { email() }
            }
        }

        val validationResult = Person("John Smith", "john@test.com").validate()
    }
}) {
    private data class Entity(
        val entityType: String,
        val registeredOffice: String,
        val proofOfId: String
    )

    private data class MyObject(val name: String, val age: Int) : ValidObject<MyObject> {
        override val validation: Validation<MyObject> = Validation {
            MyObject::name { notBlank() }
            MyObject::age { gte(18) }
        }
    }

    enum class HttpStatus {
        OK,
        BAD_REQUEST
    }
}
