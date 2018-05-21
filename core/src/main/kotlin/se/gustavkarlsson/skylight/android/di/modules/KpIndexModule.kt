package se.gustavkarlsson.skylight.android.di.modules

import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.services.providers.KpIndexProvider

interface KpIndexModule {
	val kpIndexProvider: KpIndexProvider
	val kpIndexFlowable: Flowable<KpIndex>
}
