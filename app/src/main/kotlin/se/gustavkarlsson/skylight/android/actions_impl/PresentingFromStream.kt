package se.gustavkarlsson.skylight.android.actions_impl

import se.gustavkarlsson.skylight.android.services.Presenter
import se.gustavkarlsson.skylight.android.services.streams.Stream
import se.gustavkarlsson.skylight.android.services.streams.StreamSubscription

abstract class PresentingFromStream<T>(
	private val stream: Stream<T>,
	private val presenter: Presenter<T>
) {

	private var subscription: StreamSubscription? = null

	@Synchronized fun start() {
		require(!started) { "Already started" }
		subscription = stream.subscribe {
			presenter.present(it)
		}
	}

	@Synchronized fun stop() {
		require(started) { "Not started" }
		subscription!!.cancel()
		subscription = null
	}

	private val started: Boolean
		get() = subscription != null
}
