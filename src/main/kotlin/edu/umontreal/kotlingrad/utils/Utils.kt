package edu.umontreal.kotlingrad.utils

infix fun ClosedRange<Double>.step(step: Double): Iterable<Double> {
  require(start.isFinite())
  require(endInclusive.isFinite())
  require(step > 0.0) { "Step must be positive, was: $step." }
  val sequence = generateSequence(start) { previous ->
    if (previous == Double.POSITIVE_INFINITY) return@generateSequence null
    val next = previous + step
    if (next > endInclusive) null else next
  }
  return sequence.asIterable()
}

fun <T> Iterable<T>.repeat(n: Int) = sequence { repeat(n) { yieldAll(this@repeat) } }

fun randomDefaultName() = ('a'..'z').map { it }.shuffled().subList(0, 4).joinToString("")