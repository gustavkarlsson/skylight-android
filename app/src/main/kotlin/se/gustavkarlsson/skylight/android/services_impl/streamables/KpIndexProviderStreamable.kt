package se.gustavkarlsson.skylight.android.services_impl.streamables

import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.KpIndexProvider
import timber.log.Timber

class KpIndexProviderStreamable(
	kpIndexProvider: KpIndexProvider
) : Streamable<KpIndex> {
	override val stream: Flowable<KpIndex> = kpIndexProvider.get()
		.repeatWhen { it.delay(POLLING_INTERVAL) }
		.retryWhen { it.delay(RETRY_DELAY) }
		.doOnNext { Timber.i("Streamed Kp index: %s", it) }

	companion object {
		val POLLING_INTERVAL = 15.minutes
		val RETRY_DELAY = 10.seconds
	}
}
