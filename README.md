# Validk
![GitHub release (latest by date)](https://img.shields.io/github/v/release/resoluteworks/validk)
![Coveralls](https://img.shields.io/coverallsCoverage/github/resoluteworks/validk)

Validk is a validation framework for Kotlin (JVM), largely inspired by [Konform](https://github.com/konform-kt/konform). Among other things,
the design aims to solve use cases like context-aware and conditional validations.

The framework provides a typesafe DSL and has zero dependencies.

## Dependency
```groovy
implementation "io.resoluteworks:validk:${validkVersion}"
```

## The basics

```kotlin
data class Employee(val name: String, val email: String?)
data class Organisation(val name: String, val employees: List<Employee>)

val validation = Validation {
    Organisation::name { minLength(5) }
    Organisation::employees each {
        Employee::name { minLength(10) }
        Employee::email ifNotNull { email() }
    }
}

val org = Organisation(
    name = "ACME",
    employees = listOf(Employee("John", "john@test.com"), Employee("Hannah Johnson", "hanna"))
)
val errors = validation.validate(org)
errors?.errors?.forEach { println(it) }
```

This would print
```text
ValidationError(propertyPath=name, message=must be at least 5 characters)
ValidationError(propertyPath=employees[0].name, message=must be at least 10 characters)
ValidationError(propertyPath=employees[1].email, message=must be a valid email)
```

Validating an object returns a `ValidationErrors` which is `null` when validation succeeds.
In other words, validation is successful when the response is `null`, and an instance of `ValidationErrors` when it fails.   

The object `ValidationErrors` contains a set of utilities to navigate the error messages for the failed properties.
```kotlin
// A list of ValidationError objects
errors.validationErrors

// All validation errors for property path employees[0].name (List<String>)
errors.errors("employees[0].name")

// The first error for property path employees[0].name (String)
errors.error("employees[0].name")

// Check whether a property path has any validation errors (Boolean)
errors.hasErrors("employees[0].name")

// List of all property paths that have validation errors (Set<String>)
errors.failedProperties
```

Please check the [tests](https://github.com/resoluteworks/validk/tree/main/validk/src/test/kotlin/io/validk) for more examples and the [documentation](https://resoluteworks.github.io/validk/validk/validk/io.validk/index.html) for a full list of constraints.

## Custom messages
```kotlin
Validation<Person> {
    Person::name {
        notBlank() message "A person needs a name"
        matches("[a-zA-Z\\s]+") message "Letters only please"
    }
}
```

## Context-aware and conditional validation
Validk provides the ability to access the object being validated using the `withValue` construct.
```kotlin
private data class Entity(
    val entityType: String,
    val registeredOffice: String,
    val proofOfId: String
)

private enum class EntityType { COMPANY, PERSON }

val validation: Validation<Entity> = Validation<Entity> {
    Entity::entityType { enum<EntityType>() }
    withValue { entity ->
        when (entity.entityType) {
            "PERSON" -> Entity::proofOfId { minLength(10) }
            "COMPANY" -> Entity::registeredOffice { minLength(5) }
        }
    }
}
```

Alternatively, you can add validation logic based on the value of a specific property using `whenIs`.
```kotlin
val validation: Validation<Entity> = Validation {
    Entity::entityType { enum<EntityType>() }

    Entity::entityType.whenIs("PERSON") {
        Entity::proofOfId { minLength(10) }
    }

    Entity::entityType.whenIs("COMPANY") {
        Entity::registeredOffice { minLength(5) }
    }
}
```
## Validation with return type
You can return a custom validation outcomes by handling the `error` and `success` states
of a validation call. Below is an example returning  a `Boolean` depending on the
validation result.

```kotlin
data class Person(val name: String, val age: Int)

val validation = Validation {
    Person::name { notBlank() }
    Person::age { min(18) }
}

val result: Boolean = validation.validate(Person(name = "John Smith", age = 12)) {
    error { person, errors -> false }
    success { true }
}
```

## Fail-fast validation
It's often required to only return the first failure (failed constraint) message when validating a field.
This is usually the case when displaying user errors in an application and when the order of the constraints
implies the next one would fail.

For example `notBlank()` failing means that `email()` will fail, and we want to respond with "Email is required"
rather than ["Email is required", "This is not a valid email"].

We call this fail-fast validation and it's enabled by default. Fail-fast validation can be configured when creating
the `Validation` object. The example below will check all the constraints and return errors for each one that fails.
```kotlin
Validation {
    failFast(false)
    Person::name {
        notBlank()
        matches("[a-zA-Z]+ [a-zA-Z]+")
    }
}
```

When turning fail-fast off, you can still opt to only select the first error message post-validation, by using `ValidationErrors.error(propertyPath)`.
For more details on `ValidationErrors` please check the [ValiationErrors docs](https://resoluteworks.github.io/validk/validk/validk/io.validk/-validation-errors/index.html)

## ValidObject
`ValidObject` provides a basic mechanism for storing the validation logic within the object itself.
```kotlin
data class MyObject(val name: String, val age: Int) : ValidObject<MyObject> {
    override val validation = Validation {
        MyObject::name { notBlank() }
        MyObject::age { min(18) }
    }
}

val result = MyObject("John Smith", 12).validate()
```

