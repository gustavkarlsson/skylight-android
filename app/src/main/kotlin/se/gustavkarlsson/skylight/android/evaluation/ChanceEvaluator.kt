package se.gustavkarlsson.skylight.android.evaluation

// TODO Add 'in' variance
interface ChanceEvaluator<T> {
    fun evaluate(value: T): Chance
}
