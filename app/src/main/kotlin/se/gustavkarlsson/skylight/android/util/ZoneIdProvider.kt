package se.gustavkarlsson.skylight.android.util

import org.threeten.bp.ZoneId

// TODO replace with () -> ZoneId
interface ZoneIdProvider {
	val zoneId: ZoneId
}

