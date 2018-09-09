package se.gustavkarlsson.skylight.android.modules

import io.reactivex.Flowable
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.DarknessProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.KlausBrunnerDarknessProvider
import se.gustavkarlsson.skylight.android.services_impl.streamables.DarknessProviderStreamable

val darknessModule = module {

	single<DarknessProvider> {
		KlausBrunnerDarknessProvider()
	}

	single<Streamable<Darkness>>("darkness") {
		val locations = get<Flowable<Location>>("location")
		DarknessProviderStreamable(locations, get(), get(), 1.minutes, 5.seconds)
	}

	single<Flowable<Darkness>>("darkness") {
		get<Streamable<Darkness>>("darkness")
			.stream
			.replay(1)
			.refCount()
	}

}
