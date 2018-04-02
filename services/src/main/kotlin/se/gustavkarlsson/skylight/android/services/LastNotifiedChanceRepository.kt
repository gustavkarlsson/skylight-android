package se.gustavkarlsson.skylight.android.services

import se.gustavkarlsson.skylight.android.entities.NotifiedChance

interface LastNotifiedChanceRepository {
	fun get(): NotifiedChance?
	fun insert(notifiedChance: NotifiedChance)
}
