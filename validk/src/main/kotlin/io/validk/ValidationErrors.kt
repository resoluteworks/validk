package io.validk

data class ValidationErrors(val errors: List<ValidationError>) {

    constructor(propertyPath: String, errorMessage: String) : this(listOf(ValidationError(propertyPath, errorMessage)))

    val errorMessages: Map<String, List<String>> =
        errors.groupBy { it.propertyPath }
            .map { it.key to it.value.map { error -> error.message } }
            .toMap()

    val eagerErrors: List<ValidationError> = errors.eagerErrors()
    val eagerErrorMessages: Map<String, String> = eagerErrors.associate { it.propertyPath to it.message }
    val failedProperties: Set<String> = errors.map { it.propertyPath }.toSet()
    fun failed(propertyPath: String): Boolean = failedProperties.contains(propertyPath)
}

private fun List<ValidationError>.eagerErrors(): List<ValidationError> {
    return this.groupBy { it.propertyPath }
        .map { it.value.first() }
}
