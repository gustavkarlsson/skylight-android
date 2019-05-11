package se.gustavkarlsson.skylight.android.darkness

import io.reactivex.Flowable
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.entities.Report
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.DarknessProvider

val darknessModule = module {

	single<DarknessProvider> {
		KlausBrunnerDarknessProvider(get(), 1.minutes)
	}

	single<Streamable<Report<Darkness>>>("darkness") {
		val locations = get<Flowable<Location>>("location")
		DarknessProviderStreamable(locations, get(), 1.minutes)
	}

	single<Flowable<Report<Darkness>>>("darkness") {
		get<Streamable<Report<Darkness>>>("darkness")
			.stream
			.replay(1)
			.refCount()
	}

}
