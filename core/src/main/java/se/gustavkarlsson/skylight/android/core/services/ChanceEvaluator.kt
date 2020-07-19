package se.gustavkarlsson.skylight.android.core.services

import se.gustavkarlsson.skylight.android.core.entities.Chance

interface ChanceEvaluator<T> {
    fun evaluate(value: T): Chance
}
