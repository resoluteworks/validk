package io.validk

import io.kotest.core.spec.style.StringSpec
import io.validk.constraints.enum
import io.validk.constraints.gte
import io.validk.constraints.minLength
import io.validk.test.shouldBeFailure
import io.validk.test.shouldBeSuccess

class DynamicValidationTest : StringSpec({

    "validate fields based on the value of another field" {
        val validation = Validation<Entity> {
            Entity::entityType { enum<EntityType>() }

            withValue { entity ->
                when (entity.entityType) {
                    "PERSON" -> {
                        Entity::proofOfId { minLength(10) }
                        Entity::age { gte(18) message "Must be at least 18" }
                    }

                    "COMPANY" -> Entity::registeredOffice { minLength(5) }
                }
            }
        }

        validation.validate(Entity("PERSON", "", "Passport number 0123456789", 12)).shouldBeFailure(
            ValidationError("age", "Must be at least 18")
        )
        validation.validate(Entity("COMPANY", "", "", 12)).shouldBeFailure(
            ValidationError("registeredOffice", "Must be at least 5 characters long")
        )

        validation.validate(Entity("PERSON", "", "Passport number 0123456789", 24)).shouldBeSuccess()
        validation.validate(Entity("COMPANY", "London", "", 1)).shouldBeSuccess()

        validation.validate(Entity("NOTHING", "London", "", 1)).shouldBeFailure(
            ValidationError("entityType", "Must be one of: COMPANY, PERSON")
        )
    }

    "validate fields based on the value of another field with whenIs" {
        val validation = Validation {
            Entity::entityType { enum<EntityType>() }

            Entity::entityType.whenIs("PERSON") {
                Entity::proofOfId { minLength(10) }
                Entity::age { gte(18) message "Must be at least 18" }
            }

            Entity::entityType.whenIs("COMPANY") {
                Entity::registeredOffice { minLength(5) }
            }
        }

        validation.validate(Entity("PERSON", "", "Passport number 0123456789", 12)).shouldBeFailure(
            ValidationError("age", "Must be at least 18")
        )
        validation.validate(Entity("COMPANY", "", "", 12)).shouldBeFailure(
            ValidationError("registeredOffice", "Must be at least 5 characters long")
        )

        validation.validate(Entity("PERSON", "", "Passport number 0123456789", 24)).shouldBeSuccess()
        validation.validate(Entity("COMPANY", "London", "", 1)).shouldBeSuccess()

        validation.validate(Entity("NOTHING", "London", "", 1)).shouldBeFailure(
            ValidationError("entityType", "Must be one of: COMPANY, PERSON")
        )
    }
}) {

    private data class Entity(
        val entityType: String,
        val registeredOffice: String,
        val proofOfId: String,
        val age: Int
    )

    private enum class EntityType {
        COMPANY,
        PERSON
    }
}
