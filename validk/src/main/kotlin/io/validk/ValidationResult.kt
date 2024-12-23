package io.validk

/**
 * Represents the result of a validation.
 */
sealed class ValidationResult<Value>(val value: Value) {

    /**
     * Represents a successful validation.
     */
    class Success<Value>(value: Value) : ValidationResult<Value>(value)

    /**
     * Represents a failed validation.
     *
     * @property validationErrors The validation errors.
     */
    class Failure<Value>(value: Value, val validationErrors: ValidationErrors) : ValidationResult<Value>(value) {

        constructor(value: Value, propertyPath: String, errorMessage: String) : this(value, ValidationErrors(propertyPath, errorMessage))
        constructor(value: Value, errors: List<ValidationError>) : this(value, ValidationErrors(errors))
        constructor(value: Value, vararg errors: ValidationError) : this(value, ValidationErrors(*errors))

        val allErrors = validationErrors.errors
    }

    fun ifFailed(block: (ValidationErrors) -> Unit) {
        if (this is Failure) {
            block(validationErrors)
        }
    }

    fun <Result : Any> map(
        success: (Value) -> Result,
        error: (Value, ValidationErrors) -> Result
    ): Result = when (this) {
        is Success -> success(value)
        is Failure -> error(value, validationErrors)
    }

    fun <Result : Any> map(block: ResultMapContext<Value, Result>.() -> Unit): Result {
        val context = ResultMapContext<Value, Result>()
        context.block()
        return map(
            success = context.success ?: throw IllegalStateException("Success handler missing"),
            error = context.error ?: throw IllegalStateException("Error handler missing")
        )
    }

    class ResultMapContext<Value, Result>(
        internal var success: ((Value) -> Result)? = null,
        internal var error: ((Value, ValidationErrors) -> Result)? = null
    ) {

        fun error(onError: (Value, ValidationErrors) -> Result) {
            this.error = onError
        }

        fun success(onSuccess: (Value) -> Result) {
            this.success = onSuccess
        }
    }
}
