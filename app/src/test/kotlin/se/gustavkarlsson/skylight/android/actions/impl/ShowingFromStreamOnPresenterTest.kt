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
import se.gustavkarlsson.skylight.android.actions_impl.ShowingFromStreamOnPresenter
import se.gustavkarlsson.skylight.android.services.Presenter
import se.gustavkarlsson.skylight.android.services.streams.Stream
import se.gustavkarlsson.skylight.android.services_impl.streams.RxStream

@RunWith(MockitoJUnitRunner::class)
class ShowingFromStreamOnPresenterTest {

	lateinit var showingFromStreamOnPresenter: ShowingFromStreamOnPresenter<Int>

	lateinit var stream: Stream<Int>

	lateinit var subject: Subject<Int>

	@Mock
	lateinit var presenter: Presenter<Int>

	@Before
	fun setUp() {
		showingFromStreamOnPresenter = ShowingFromStreamOnPresenter(stream, presenter)
		stream = RxStream(subject)
		subject = PublishSubject.create()
	}

	@Test
	fun canStartFromDefaultState() {
		showingFromStreamOnPresenter.start()
	}

	@Test(expected = IllegalArgumentException::class)
	fun canNotStopFromDefaultState() {
		showingFromStreamOnPresenter.stop()
	}

	@Test(expected = IllegalArgumentException::class)
	fun canNotStartFromStartedState() {
		showingFromStreamOnPresenter.start()
		showingFromStreamOnPresenter.start()
	}

	@Test
	fun canStopFromStartedState() {
		showingFromStreamOnPresenter.start()
		showingFromStreamOnPresenter.stop()
	}

	@Test
	fun stoppedStateDoesNotPresentElementsFromStream() {
		subject.onNext(5)
		verifyZeroInteractions(presenter)
	}

	@Test
	fun startedStateDoesPresentElementsFromStream() {
		showingFromStreamOnPresenter.start()
		subject.onNext(5)
		verify(presenter).present(5)
	}
}
