package se.gustavkarlsson.skylight.android.services.impl

import io.reactivex.disposables.Disposable
import se.gustavkarlsson.skylight.android.services.StreamSubscription

class RxStreamSubscription(private val disposable: Disposable) : StreamSubscription {
	override fun cancel() {
		disposable.dispose()
	}
}
