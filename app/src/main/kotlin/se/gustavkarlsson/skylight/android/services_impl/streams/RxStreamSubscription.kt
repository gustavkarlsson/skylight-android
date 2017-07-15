package se.gustavkarlsson.skylight.android.services_impl.streams

import io.reactivex.disposables.Disposable
import se.gustavkarlsson.skylight.android.services.streams.StreamSubscription

class RxStreamSubscription(private val disposable: Disposable) : StreamSubscription {
	override fun cancel() {
		disposable.dispose()
	}
}
