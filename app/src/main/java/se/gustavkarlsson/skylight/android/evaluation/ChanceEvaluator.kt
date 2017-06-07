package se.gustavkarlsson.skylight.android.evaluation

// TODO Make in-variance
internal interface ChanceEvaluator<T> {
    fun evaluate(value: T): Chance
}
