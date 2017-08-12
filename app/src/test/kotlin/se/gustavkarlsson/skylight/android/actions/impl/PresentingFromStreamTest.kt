package se.gustavkarlsson.skylight.android.actions.impl

import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyZeroInteractions
import org.mockito.junit.MockitoJUnitRunner
import se.gustavkarlsson.skylight.android.actions_impl.PresentingFromStream
import se.gustavkarlsson.skylight.android.services.Presenter
import se.gustavkarlsson.skylight.android.services.streams.Stream
import se.gustavkarlsson.skylight.android.services_impl.streams.RxStream

@RunWith(MockitoJUnitRunner::class)
class PresentingFromStreamTest {

	lateinit var presentingFromStream: PresentingFromStream<Int>

	lateinit var stream: Stream<Int>

	lateinit var subject: Subject<Int>

	@Mock
	lateinit var presenter: Presenter<Int>

	@Before
	fun setUp() {
		presentingFromStream = object : PresentingFromStream<Int>(stream, presenter) {}
		stream = RxStream(subject)
		subject = PublishSubject.create()
	}

	@Test
	fun canStartFromDefaultState() {
		presentingFromStream.start()
	}

	@Test(expected = IllegalArgumentException::class)
	fun canNotStopFromDefaultState() {
		presentingFromStream.stop()
	}

	@Test(expected = IllegalArgumentException::class)
	fun canNotStartFromStartedState() {
		presentingFromStream.start()
		presentingFromStream.start()
	}

	@Test
	fun canStopFromStartedState() {
		presentingFromStream.start()
		presentingFromStream.stop()
	}

	@Test
	fun stoppedStateDoesNotPresentElementsFromStream() {
		subject.onNext(5)
		verifyZeroInteractions(presenter)
	}

	@Test
	fun startedStateDoesPresentElementsFromStream() {
		presentingFromStream.start()
		subject.onNext(5)
		verify(presenter).present(5)
	}
}
