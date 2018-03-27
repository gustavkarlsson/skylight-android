package se.gustavkarlsson.skylight.android.services

import se.gustavkarlsson.skylight.android.entities.Chance

interface ChanceEvaluator<T> {
    fun evaluate(value: T): Chance
}
