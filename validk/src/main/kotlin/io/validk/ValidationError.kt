package io.validk

/**
 * Represents a validation error.
 *
 * @property path The path to the property that caused the error.
 * @property message The error message.
 */
data class ValidationError(
    val path: String,
    val message: String
) {

    /**
     * Returns a new [ValidationError] based on the current one with the property path indexed by [index].
     *
     * @param property The name of the property causing the error.
     * @param index The index to use for the property path.
     *
     * @return A new [ValidationError] with the property path indexed by [index].
     */
    internal fun indexed(property: String, index: Int): ValidationError = ValidationError(
        path = this.path.indexedProperty(property, index),
        message = message
    )

    companion object {
        /**
         * Internal function to create an indexed property path in the form of `property[index]`.
         */
        internal fun String.indexedProperty(
            property: String,
            index: Int
        ): String = this.replace("""${property}(\.|$)""".toRegex(), "${property}[$index]$1")
    }
}
