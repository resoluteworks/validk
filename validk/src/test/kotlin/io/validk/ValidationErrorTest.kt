package io.validk

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.validk.ValidationError.Companion.indexedProperty

class ValidationErrorTest : StringSpec({

    "indexed property" {
        "name".indexedProperty("name", 1) shouldBe "name[1]"
        "name.child".indexedProperty("name", 1) shouldBe "name[1].child"
        "address.postCodes.value".indexedProperty("postCodes", 5) shouldBe "address.postCodes[5].value"
    }

    "ValidationError.indexed" {
        val error = ValidationError("name", "Must not be blank").indexed("name", 1)
        error shouldBe ValidationError("name[1]", "Must not be blank")
    }
})
