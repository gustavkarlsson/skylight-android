package se.gustavkarlsson.skylight.android.actions_impl

import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyZeroInteractions
import org.mockito.junit.MockitoJUnitRunner
import se.gustavkarlsson.skylight.android.services.Presenter
import se.gustavkarlsson.skylight.android.services.streams.Stream
import se.gustavkarlsson.skylight.android.services_impl.streams.RxStream

@RunWith(MockitoJUnitRunner::class)
class PresentingFromStreamTest {

    lateinit var subject: Subject<Int>

    lateinit var stream: Stream<Int>

    @Mock
    lateinit var presenter: Presenter<Int>

    lateinit var impl: PresentingFromStream<Int>

	@Before
	fun setUp() {
		subject = PublishSubject.create()
		stream = RxStream(subject)
		impl = object : PresentingFromStream<Int>(stream, presenter) {}
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
	fun stoppedStateDoesNotPresentElementsFromStream() {
		subject.onNext(5)
		verifyZeroInteractions(presenter)
	}

	@Test
	fun startedStateDoesPresentElementsFromStream() {
		impl.start()
		subject.onNext(5)
		verify(presenter).present(5)
	}
}
