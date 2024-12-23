package io.validk.constraints

import io.validk.Validation

fun Validation<Int>.gte(value: Int) = addConstraint("Must be greater than or equal to $value") { it >= value }
fun Validation<Int>.gt(value: Int) = addConstraint("Must be greater than $value") { it > value }
fun Validation<Int>.lte(value: Int) = addConstraint("Must be less than or equal to $value") { it <= value }
fun Validation<Int>.lt(value: Int) = addConstraint("Must be less than $value") { it < value }
fun Validation<Int>.between(range: IntRange) = addConstraint("Must be between ${range.start} and ${range.endInclusive}") { it in range }

@JvmName("longGte")
fun Validation<Long>.gte(value: Long) = addConstraint("Must be greater than or equal to $value") { it >= value }

@JvmName("longGt")
fun Validation<Long>.gt(value: Long) = addConstraint("Must be greater than $value") { it > value }

@JvmName("longLte")
fun Validation<Long>.lte(value: Long) = addConstraint("Must be less than or equal to $value") { it <= value }

@JvmName("longLt")
fun Validation<Long>.lt(value: Long) = addConstraint("Must be less than $value") { it < value }

@JvmName("longBetween")
fun Validation<Long>.between(range: LongRange) = addConstraint("Must be between ${range.start} and ${range.endInclusive}") { it in range }

@JvmName("shortGte")
fun Validation<Short>.gte(value: Short) = addConstraint("Must be greater than or equal to $value") { it >= value }

@JvmName("shortGt")
fun Validation<Short>.gt(value: Short) = addConstraint("Must be greater than $value") { it > value }

@JvmName("shortLte")
fun Validation<Short>.lte(value: Long) = addConstraint("Must be less than or equal to $value") { it <= value }

@JvmName("shortLt")
fun Validation<Short>.lt(value: Short) = addConstraint("Must be less than $value") { it < value }

@JvmName("shortBetween")
fun Validation<Short>.between(range: IntRange) = addConstraint("Must be between ${range.start} and ${range.endInclusive}") { it in range }

@JvmName("floatGte")
fun Validation<Float>.gte(value: Float) = addConstraint("Must be greater than or equal to $value") { it >= value }

@JvmName("floatGt")
fun Validation<Float>.gt(value: Float) = addConstraint("Must be greater than $value") { it > value }

@JvmName("floatLte")
fun Validation<Float>.lte(value: Float) = addConstraint("Must be less than or equal to $value") { it <= value }

@JvmName("floatLt")
fun Validation<Float>.lt(value: Float) = addConstraint("Must be less than $value") { it < value }

@JvmName("doubleGte")
fun Validation<Double>.gte(value: Double) = addConstraint("Must be greater than or equal to $value") { it >= value }

@JvmName("doubleGt")
fun Validation<Double>.gt(value: Double) = addConstraint("Must be greater than $value") { it > value }

@JvmName("doubleLte")
fun Validation<Double>.lte(value: Double) = addConstraint("Must be less than or equal to $value") { it <= value }

@JvmName("doubleLt")
fun Validation<Double>.lt(value: Double) = addConstraint("Must be less than $value") { it < value }
