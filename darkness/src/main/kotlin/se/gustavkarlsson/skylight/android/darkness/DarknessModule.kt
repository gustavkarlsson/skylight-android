package se.gustavkarlsson.skylight.android.darkness

import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.services.providers.DarknessProvider

val darknessModule = module {

	single<DarknessProvider> {
		KlausBrunnerDarknessProvider(get(), 1.minutes)
	}
}
