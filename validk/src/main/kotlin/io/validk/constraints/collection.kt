package io.validk.constraints

import io.validk.Validation

@JvmName("collectionIsEmpty")
fun Validation<out Collection<*>>.notEmpty() = addConstraint("Cannot be empty") {
    it.isNotEmpty()
}

fun Validation<out Collection<*>>.minSize(minSize: Int) = addConstraint("Should have at least $minSize elements") {
    it.size >= minSize
}

fun Validation<out Collection<*>>.maxSize(maxSize: Int) = addConstraint("Should have at most $maxSize elements") {
    it.size <= maxSize
}
