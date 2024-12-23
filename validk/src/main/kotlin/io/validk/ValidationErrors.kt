package io.validk

/**
 * Represents the validation errors for a validation failure.
 *
 * @property errors The list of validation errors.
 */
data class ValidationErrors(val errors: List<ValidationError>) {

    /**
     * Creates a new [ValidationErrors] instance with a single error.
     */
    constructor(path: String, message: String) : this(listOf(ValidationError(path, message)))

    /**
     * Convenience vararg constructor
     */
    constructor(vararg errors: ValidationError) : this(errors.toList())

    /**
     * Returns the validation errors as a map where the key is the property path and
     * the value is a list of error messages for that property.
     */
    val errorsByPath: Map<String, List<String>> =
        errors.groupBy { it.path }
            .map { it.key to it.value.map { error -> error.message } }
            .toMap()

    /**
     * Returns all error messages for the provided [path] or an empty list if
     * there are no errors for it.
     */
    fun errors(path: String): List<String> = errorsByPath[path] ?: emptyList()

    /**
     * Returns the first error message for the provided [path] or
     * null if there are no errors for it.
     */
    fun error(path: String): String? = errorsByPath[path]?.firstOrNull()

    /**
     * Set of property paths containing errors.
     */
    val failedProperties: Set<String> = errors.map { it.path }.toSet()

    /**
     * Checks if there are validation errors for a property path.
     *
     * @param propertyPath The property path to check for errors.
     * @return `true` if there are errors for the property path, `false` otherwise.
     */
    fun hasErrors(propertyPath: String): Boolean = failedProperties.contains(propertyPath)
}
