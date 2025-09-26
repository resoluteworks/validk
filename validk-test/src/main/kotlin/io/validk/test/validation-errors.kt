package io.validk.test

import io.kotest.assertions.withClue
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.shouldNotBe
import io.validk.ValidationErrors

fun ValidationErrors.shouldHaveError(path: String, message: String? = null) {
    if (message != null) {
        withClue("Expected to find error with path '$path' and message '$message' in $this") {
            this.errors.find { it.path == path && it.message == message } shouldNotBe null
        }
    } else {
        withClue("Expected to find error with path '$path' in $this") {
            this.errors.find { it.path == path } shouldNotBe null
        }
    }
}

fun ValidationErrors.shouldHaveErrors(vararg paths: String) {
    paths.shouldForAll { path ->
        shouldHaveError(path)
    }
}
