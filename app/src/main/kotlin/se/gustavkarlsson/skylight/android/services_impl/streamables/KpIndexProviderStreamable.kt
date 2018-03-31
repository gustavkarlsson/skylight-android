package se.gustavkarlsson.skylight.android.services_impl.streamables

import io.reactivex.Flowable
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.KpIndexProvider
import timber.log.Timber
import java.util.concurrent.TimeUnit

class KpIndexProviderStreamable(
	kpIndexProvider: KpIndexProvider,
	pollingInterval: Duration,
	retryDelay: Duration
) : Streamable<KpIndex> {
	override val stream: Flowable<KpIndex> = kpIndexProvider.get()
		.repeatWhen { it.delay(pollingInterval.toMillis(), TimeUnit.MILLISECONDS) }
		.retryWhen { it.delay(retryDelay.toMillis(), TimeUnit.MILLISECONDS) }
		.doOnNext { Timber.i("Streamed Kp index: %s", it) }
}
