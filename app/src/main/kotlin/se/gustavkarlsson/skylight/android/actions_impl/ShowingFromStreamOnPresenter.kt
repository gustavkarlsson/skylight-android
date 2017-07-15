package se.gustavkarlsson.skylight.android.actions_impl

import se.gustavkarlsson.skylight.android.services.Presenter
import se.gustavkarlsson.skylight.android.services.streams.Stream
import se.gustavkarlsson.skylight.android.services.streams.StreamSubscription

abstract class ShowingFromStreamOnPresenter<T>(
	private val stream: Stream<T>,
	private val presenter: Presenter<T>
) {
	private var subscription: StreamSubscription? = null

	@Synchronized fun start() {
		require(subscription == null, { "Already subscribed" })
		subscription = stream.subscribe {
			presenter.present(it)
		}
	}

	@Synchronized fun stop() {
		require(subscription != null, { "Not subscribed" })
		subscription!!.cancel()
		subscription = null
	}
}
