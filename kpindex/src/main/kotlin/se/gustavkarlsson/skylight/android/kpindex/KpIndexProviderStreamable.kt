package se.gustavkarlsson.skylight.android.kpindex

import io.reactivex.Flowable
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.entities.Report
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.KpIndexProvider
import timber.log.Timber

class KpIndexProviderStreamable(
	kpIndexProvider: KpIndexProvider,
	pollingInterval: Duration
) : Streamable<Report<KpIndex>> {
	override val stream: Flowable<Report<KpIndex>> = kpIndexProvider.get()
		.repeatWhen { it.delay(pollingInterval) }
		.distinctUntilChanged()
		.doOnNext { Timber.i("Streamed Kp index: %s", it) }
}
