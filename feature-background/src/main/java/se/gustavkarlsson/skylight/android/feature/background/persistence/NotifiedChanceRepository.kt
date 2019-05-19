package se.gustavkarlsson.skylight.android.feature.background.persistence

import se.gustavkarlsson.skylight.android.entities.NotifiedChance

internal interface NotifiedChanceRepository {
	fun get(): NotifiedChance?
	fun insert(notifiedChance: NotifiedChance)
}
