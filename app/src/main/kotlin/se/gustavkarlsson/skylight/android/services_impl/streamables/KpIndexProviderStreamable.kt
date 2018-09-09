package se.gustavkarlsson.skylight.android.services_impl.streamables

import io.reactivex.Flowable
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.KpIndexProvider
import timber.log.Timber

class KpIndexProviderStreamable(
	kpIndexProvider: KpIndexProvider,
	pollingInterval: Duration,
	retryDelay: Duration
) : Streamable<KpIndex> {
	override val stream: Flowable<KpIndex> = kpIndexProvider.get()
		.repeatWhen { it.delay(pollingInterval) }
		.retryWhen { it.delay(retryDelay) }
		.doOnNext { Timber.i("Streamed Kp index: %s", it) }
}
