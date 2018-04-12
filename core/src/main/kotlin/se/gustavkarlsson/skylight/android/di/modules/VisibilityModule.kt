package se.gustavkarlsson.skylight.android.di.modules

import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.entities.Visibility
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.VisibilityProvider

interface VisibilityModule {
	val visibilityProvider: VisibilityProvider
	val visibilityStreamable: Streamable<Visibility>
	val visibilityFlowable: Flowable<Visibility>
}
