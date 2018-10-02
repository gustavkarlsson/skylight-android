package se.gustavkarlsson.skylight.android.modules

import org.koin.dsl.module.module
import org.threeten.bp.ZoneId
import se.gustavkarlsson.skylight.android.services.providers.Time
import se.gustavkarlsson.skylight.android.services_impl.providers.SystemTimeWithFixedZoneId

val timeModule = module {

	single<Time> {
		SystemTimeWithFixedZoneId(ZoneId.systemDefault())
	}

}
