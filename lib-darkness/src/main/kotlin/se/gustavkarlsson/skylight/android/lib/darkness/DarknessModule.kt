package se.gustavkarlsson.skylight.android.lib.darkness

import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.services.providers.DarknessProvider

val darknessModule = module {

	single<DarknessProvider> {
		KlausBrunnerDarknessProvider(time = get(), pollingInterval = 1.minutes)
	}
}
