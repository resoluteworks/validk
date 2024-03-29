package io.validk

data class ValidationErrors(val validationErrors: List<ValidationError>) {

    constructor(propertyPath: String, errorMessage: String) : this(listOf(ValidationError(propertyPath, errorMessage)))

    /**
     * Error messages grouped by property path
     */
    val errors: Map<String, List<String>> =
        validationErrors.groupBy { it.propertyPath }
            .map { it.key to it.value.map { error -> error.message } }
            .toMap()

    /**
     * Returns all error messages for the provided [propertyPath] or empty list if there are no errors for it.
     */
    fun errors(propertyPath: String): List<String> {
        return errors[propertyPath] ?: emptyList()
    }

    /**
     * Returns all the first error message for the provided [propertyPath] or null if there are no errors for it.
     */
    fun error(propertyPath: String): String? {
        return errors[propertyPath]?.firstOrNull()
    }

    /**
     * Set of property paths containing errors
     */
    val failedProperties: Set<String> = validationErrors.map { it.propertyPath }.toSet()

    /**
     * Checks if there are validation errors for a property path
     */
    fun hasErrors(propertyPath: String): Boolean = failedProperties.contains(propertyPath)
}
