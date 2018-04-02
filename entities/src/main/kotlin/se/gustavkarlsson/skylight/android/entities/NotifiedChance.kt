package se.gustavkarlsson.skylight.android.entities

import org.threeten.bp.Instant

data class NotifiedChance(
	val chance: Chance,
	val timestamp: Instant
)
