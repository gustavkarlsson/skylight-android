package se.gustavkarlsson.skylight.android

import org.koin.dsl.module.module
import org.threeten.bp.ZoneId
import se.gustavkarlsson.skylight.android.services.Time
import se.gustavkarlsson.skylight.android.time.SystemTimeWithFixedZoneId

val coreModule = module {

	single<Time> {
		SystemTimeWithFixedZoneId(ZoneId.systemDefault())
	}
}
