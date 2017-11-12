package se.gustavkarlsson.skylight.android.actions_impl

import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyZeroInteractions
import org.mockito.junit.MockitoJUnitRunner
import se.gustavkarlsson.skylight.android.services.Presenter

@RunWith(MockitoJUnitRunner::class)
class PresentingFromObservableTest {

	lateinit var relay: Relay<Int>

	lateinit var observable: Observable<Int>

	@Mock
	lateinit var presenter: Presenter<Int>

	lateinit var impl: PresentingFromObservable<Int>

	@Before
	fun setUp() {
		relay = PublishRelay.create()
		observable = relay
		impl = object : PresentingFromObservable<Int>(observable, presenter) {}
	}

	@Test
	fun canStartFromDefaultState() {
		impl.start()
	}

	@Test(expected = IllegalArgumentException::class)
	fun canNotStopFromDefaultState() {
		impl.stop()
	}

	@Test(expected = IllegalArgumentException::class)
	fun canNotStartFromStartedState() {
		impl.start()
		impl.start()
	}

	@Test
	fun canStopFromStartedState() {
		impl.start()
		impl.stop()
	}

	@Test
	fun stoppedStateDoesNotPresentElementsFromObservable() {
		relay.accept(5)
		verifyZeroInteractions(presenter)
	}

	@Test
	fun startedStateDoesPresentElementsFromObservable() {
		impl.start()
		relay.accept(5)
		verify(presenter).present(5)
	}
}
