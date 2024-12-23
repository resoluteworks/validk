package io.validk.constraints

import io.validk.Validation

private val REGEX_EMAIL = ".+@.+\\..+".toRegex()

fun Validation<String>.notEmpty() = addConstraint("Cannot be empty") {
    it.isNotEmpty()
}

fun Validation<String>.notBlank() = addConstraint("Cannot be blank") {
    it.isNotBlank()
}

fun Validation<String>.minLength(minLength: Int) = addConstraint("Must be at least $minLength characters long") {
    it.length >= minLength
}

fun Validation<String>.maxLength(maxLength: Int) = addConstraint("Must be at most $maxLength characters long") {
    it.length <= maxLength
}

fun Validation<String>.enum(vararg values: String) = addConstraint("Must be one of: ${values.joinToString(", ")}") {
    it in values
}

inline fun <reified T : Enum<T>> Validation<String>.enum() = enum(*enumValues<T>().map { it.name }.toTypedArray())

fun Validation<String>.matches(regex: Regex) = addConstraint("Must match pattern ${regex.pattern}") {
    it.matches(regex)
}

fun Validation<String>.matches(regexPattern: String) = matches(regexPattern.toRegex())

fun Validation<String>.email() = matches(REGEX_EMAIL) message "Must be a valid email"
