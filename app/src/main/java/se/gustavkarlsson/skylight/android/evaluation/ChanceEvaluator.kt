package se.gustavkarlsson.skylight.android.evaluation

// TODO Make in-variance
interface ChanceEvaluator<T> {
    fun evaluate(value: T): Chance
}
