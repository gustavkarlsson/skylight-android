package se.gustavkarlsson.skylight.android.services.evaluation

interface ChanceEvaluator<T> {
    fun evaluate(value: T): Chance
}
