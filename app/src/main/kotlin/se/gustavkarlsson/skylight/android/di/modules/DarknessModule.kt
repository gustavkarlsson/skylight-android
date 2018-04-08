package se.gustavkarlsson.skylight.android.di.modules

import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.DarknessProvider

interface DarknessModule {
	val darknessProvider: DarknessProvider
	val darknessStreamable: Streamable<Darkness>
	val darknessFlowable: Flowable<Darkness>
}
