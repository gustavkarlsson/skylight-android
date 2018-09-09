package se.gustavkarlsson.skylight.android.modules

import org.koin.dsl.module.module
import org.threeten.bp.ZoneId
import se.gustavkarlsson.skylight.android.services.providers.TimeProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.SystemTimeWithFixedZoneIdProvider

val timeModule = module {

	single<TimeProvider> {
		SystemTimeWithFixedZoneIdProvider(ZoneId.systemDefault())
	}

}
