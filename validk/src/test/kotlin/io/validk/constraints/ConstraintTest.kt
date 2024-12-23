package io.validk.constraints

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.validk.ValidationError

class ConstraintTest : StringSpec({

    "constructor - error message as lambda" {
        Constraint<String>(
            { it.length >= 5 },
            { "'$it' is shorter than 5 characters" }
        ).check("name", "John") shouldBe ValidationError("name", "'John' is shorter than 5 characters")
    }

    "constructor - error message as string" {
        Constraint<String>("Must be at least 5 characters long") { it.length >= 5 }
            .check("name", "John") shouldBe ValidationError("name", "Must be at least 5 characters long")
    }

    "message override - lambda" {
        val constraint = Constraint<String>("Must be at least 5 characters long") { it.length >= 5 }
        (constraint message { "'$it' is shorter than 5 characters" })
            .check("name", "John") shouldBe ValidationError("name", "'John' is shorter than 5 characters")
    }

    "message override - string" {
        val constraint = Constraint<String>("Must be at least 5 characters long") { it.length >= 5 }
        (constraint message "Name must be at least 5 characters long")
            .check("name", "John") shouldBe ValidationError("name", "Name must be at least 5 characters long")
    }

    "returns null when predicate passes" {
        Constraint<String>("Must be at least 5 characters long") { it.length >= 5 }
            .check("name", "John Smith") shouldBe null
    }
})
