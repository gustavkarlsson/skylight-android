package se.gustavkarlsson.skylight.android.lib.darkness

import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.extensions.minutes

val libDarknessModule = module {

	single<DarknessProvider> {
		KlausBrunnerDarknessProvider(time = get(), pollingInterval = 1.minutes)
	}
}
