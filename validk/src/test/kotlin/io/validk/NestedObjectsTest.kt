package io.validk

import io.kotest.core.spec.style.StringSpec
import io.validk.constraints.email
import io.validk.constraints.enum
import io.validk.constraints.minLength
import io.validk.constraints.notBlank
import io.validk.constraints.notEmpty
import io.validk.test.shouldBeFailure
import io.validk.test.shouldBeSuccess

class NestedObjectsTest : StringSpec({

    "nested collection" {
        data class Child(val childName: String)
        data class Parent(val name: String, val children: List<Child>)

        val validation = Validation<Parent> {
            Parent::name { notBlank() }
            Parent::children { notEmpty() message "children list cannot be empty" }
            Parent::children each {
                Child::childName { notBlank() }
            }
        }

        validation.validate(Parent("John Smith", emptyList())).shouldBeFailure(
            ValidationError("children", "children list cannot be empty")
        )
        validation.validate(Parent("John Smith", listOf(Child("One"), Child("Two")))).shouldBeSuccess()
        validation.validate(Parent("John Smith", listOf(Child(""), Child("Two"), Child("")))).shouldBeFailure(
            ValidationError("children[0].childName", "Cannot be blank"),
            ValidationError("children[2].childName", "Cannot be blank")
        )
    }

    "nested object" {
        data class Address(val city: String, val postCode: String)
        data class Person(val name: String, val address: Address)

        val validation = Validation {
            Person::name { notBlank() }
            Person::address {
                Address::city { notBlank() }
                Address::postCode { minLength(4) }
            }
        }

        validation.validate(Person("", Address("London", "ABCD"))).shouldBeFailure(
            ValidationError("name", "Cannot be blank")
        )
        validation.validate(Person("", Address("", "ABCD"))).shouldBeFailure(
            ValidationError("name", "Cannot be blank"),
            ValidationError("address.city", "Cannot be blank")
        )
        validation.validate(Person("", Address("", "ABC"))).shouldBeFailure(
            ValidationError("name", "Cannot be blank"),
            ValidationError("address.city", "Cannot be blank"),
            ValidationError("address.postCode", "Must be at least 4 characters long")
        )

        validation.validate(Person("John Smith", Address("", "ABC"))).shouldBeFailure(
            ValidationError("address.city", "Cannot be blank"),
            ValidationError("address.postCode", "Must be at least 4 characters long")
        )

        validation.validate(Person("John Smith", Address("London", "ABCD"))).shouldBeSuccess()
    }

    "complex nested validation" {
        data class Role(val name: String, val types: List<String>)
        data class Address(val city: String, val postCode: String)
        data class Employee(
            val name: String,
            val email: String,
            val phoneNumber: String?,
            val roles: List<Role>,
            val address: Address
        )

        data class Organisation(val name: String, val employees: List<Employee>)

        val validation = Validation<Organisation> {
            Organisation::name { notBlank() }
            Organisation::employees each {
                Employee::name { notBlank() }
                Employee::email { email() }
                Employee::phoneNumber ifNotNull { minLength(6) }
                Employee::roles each {
                    Role::name { enum("DIRECTOR", "EMPLOYEE") }
                    Role::types {
                        notEmpty()
                    }
                }
                Employee::address {
                    Address::city { notBlank() }
                    Address::postCode { minLength(5) }
                }
            }
        }

        validation.validate(
            Organisation(
                "ACME",
                listOf(
                    Employee(
                        "John Smith",
                        "john@smith.com",
                        "123567890",
                        listOf(Role("DIRECTOR", listOf("employee", "leadership"))),
                        Address("London", "12345")
                    ),
                    Employee(
                        "Angela White",
                        "angela@white.com",
                        "12356789",
                        listOf(Role("DIRECTOR", emptyList())),
                        Address("London", "12")
                    )
                )
            )
        ).shouldBeFailure(
            ValidationError("employees[1].roles[0].types", "Cannot be empty"),
            ValidationError("employees[1].address.postCode", "Must be at least 5 characters long")
        )
    }
})
