# Validk

![GitHub release (latest by date)](https://img.shields.io/github/v/release/resoluteworks/validk)
![Coveralls](https://img.shields.io/coverallsCoverage/github/resoluteworks/validk)

Validk is a validation framework for Kotlin JVM designed with these goals in mind:
* Typesafe DSL to defining validation rules
* No annotations or "magic"
* Value-aware and conditional validation rules (aka dynamic validation)
* Zero dependencies

Documentation:
* [API Docs](https://resoluteworks.github.io/validk/validk/validk/io.validk/index.html)
* [Built-in constraints](https://resoluteworks.github.io/validk/validk/validk/io.validk.constraints/index.html)
* [Previous 1.x version](https://github.com/resoluteworks/validk/tree/v1.2.9)

## Dependency

```groovy
implementation "io.resoluteworks:validk:${validkVersion}"
```

## Quick start

```kotlin
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
```

The call `organisationValidation.validate(org)` returns a `ValidationResult<Organisation>` which can be either a
`ValidationResult.Success` or a `ValidationResult.Failure`. The `ValidationResult.Failure` returns the details
of the validation errors.

The code above would print the following:

```text
ValidationError(path=name, message=Must be at least 5 characters long)
ValidationError(path=employees[0].name, message=Must be at least 10 characters long)
ValidationError(path=employees[1].email, message=Must be a valid email)
```

## Error messages
Error messages can be customised with any of the following constructs.
```kotlin
// Dynamic error message
Employee::email {
    email() message { value -> "Invalid email address: $value" }
}

// Static error message
Employee::email {
    email() message "This emails address is invalid"
}
```

## Custom constraints
You can define custom constraints with by calling `addConstraint` inside a validation block.
```kotlin
Employee::name{
    addConstraint("Must start with uppercase letter") {
        it.first().isUpperCase() == true
    }
}
```

## Dynamic validation
There are several options for defining validation rules which apply in a specific context or when
the value being validated meets a certain condition.

### withValue
The `withValue` lambda receives the object being validated and allows you to define constraints based
on its properties or state.
```kotlin
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
```

### whenIs
The `whenIs` construct allows you to define constraints based on specific values for a property.
```kotlin
Validation {
    Entity::entityType.whenIs("PERSON") {
        Entity::proofOfId { minLength(10) }
    }

    Entity::entityType.whenIs("COMPANY") {
        Entity::registeredOffice { minLength(5) }
    }
}
```

## Convert validation result
A `ValidationResult` can be converted to a custom type using the `map` function. This is typically
useful when a custom application state is required as the result of a validation. A common example
would be a web application that would return a different HTTP response or status code based on the
validation result.

```kotlin
val httpResponse = validation.validate(personForm).map {
    success { person ->
        Response(HttpStatus.OK, person)
    }
    error { person, errors ->
        Response(HttpStatus.BAD_REQUEST, person, errors)
    }
}
```

## Fail-fast validation

It's often required to only return the first failure message (first failed constraint) when validating a property.
This is sometimes the case when displaying user errors in a UI, and when the order of the constraints
implies the next one would fail anyway (and thus don't need checking).

For example `notBlank()` failing means that `email()` will fail, and we want to respond with
```
"Email is required"
```
rather than
```
["Email is required", "This is not a valid email"].
```

We call this fail-fast validation and it's enabled by default. Fail-fast validation can be configured when creating
the `Validation` object. The example below will check all the constraints and return all the errors.

```kotlin
Validation {
    failFast(false)
    Person::name {
        notBlank()
        matches("[a-zA-Z]+ [a-zA-Z]+")
    }
}
```

When turning fail-fast off, you can still opt to only select the first error message post-validation, by using
`ValidationErrors.error(propertyPath)`.  For more details on `ValidationErrors` please check the [ValiationErrors docs](https://resoluteworks.github.io/validk/validk/validk/io.validk/-validation-errors/index.html).

## ValidObject

`ValidObject` provides a basic mechanism for storing the validation logic within the object itself.

```kotlin
data class Person(val name: String, val email: String) : ValidObject<Person> {
    override val validation: Validation<Person> = Validation {
        Person::name { minLength(10) }
        Person::email { email() }
    }
}

val validationResult = Person("John Smith", "john@test.com").validate()
```

## License
[Apache 2.0 License](LICENSE)
