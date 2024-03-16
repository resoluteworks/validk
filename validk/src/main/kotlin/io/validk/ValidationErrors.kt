package io.validk

data class ValidationErrors(val errors: List<ValidationError>) {

    constructor(propertyPath: String, errorMessage: String) : this(listOf(ValidationError(propertyPath, errorMessage)))

    /**
     * Error messages grouped by property path
     */
    val errorMessages: Map<String, List<String>> =
        errors.groupBy { it.propertyPath }
            .map { it.key to it.value.map { error -> error.message } }
            .toMap()

    /**
     * Eager ValidationErrors, one for each property path
     */
    val eagerErrors: List<ValidationError> = errors.eagerErrors()

    /**
     * Eager error messages keyed by property path
     */
    val eagerErrorMessages: Map<String, String> = eagerErrors.associate { it.propertyPath to it.message }

    /**
     * Set of property paths containing errors
     */
    val failedProperties: Set<String> = errors.map { it.propertyPath }.toSet()

    /**
     * Checks if there are validation errors for a property path
     */
    fun failed(propertyPath: String): Boolean = failedProperties.contains(propertyPath)
}

private fun List<ValidationError>.eagerErrors(): List<ValidationError> {
    return this.groupBy { it.propertyPath }
        .map { it.value.first() }
}
