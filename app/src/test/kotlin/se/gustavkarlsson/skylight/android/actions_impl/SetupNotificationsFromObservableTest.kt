package se.gustavkarlsson.skylight.android.actions_impl

import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.mockito.any
import se.gustavkarlsson.skylight.android.services.Notifier
import se.gustavkarlsson.skylight.android.services_impl.notifications.AuroraReportNotificationDecider

@RunWith(MockitoJUnitRunner::class)
class SetupNotificationsFromObservableTest {

	private lateinit var relay: Relay<AuroraReport>

	private lateinit var observable: Observable<AuroraReport>

	@Mock
	private lateinit var mockDecider: AuroraReportNotificationDecider

	@Mock
	private lateinit var mockNotifier: Notifier<AuroraReport>

	@Mock
	private lateinit var mockReport: AuroraReport

	private lateinit var impl: SetupNotificationsFromObservable

	@Before
	fun setUp() {
		relay = PublishRelay.create<AuroraReport>()
		observable = relay
		impl = SetupNotificationsFromObservable(observable, mockDecider, mockNotifier)
	}

	@Test(expected = IllegalStateException::class)
	fun runTwiceThrowsException() {
		impl()
		impl()
	}

	@Test
	fun whenSubscribedAndShouldNotifyCallNotify() {
		whenever(mockDecider.shouldNotify(mockReport)).thenReturn(true)

		impl()
		relay.accept(mockReport)

		verify(mockNotifier).notify(mockReport)
	}

	@Test
	fun whenSubscribedAndShouldNotifyCallOnNotified() {
		whenever(mockDecider.shouldNotify(mockReport)).thenReturn(true)

		impl()
		relay.accept(mockReport)

		verify(mockDecider).onNotified(mockReport)
	}

	@Test
	fun whenSubscribedAndShouldNotNotifyNotifyIsNotCalled() {
		whenever(mockDecider.shouldNotify(mockReport)).thenReturn(false)

		impl()
		relay.accept(mockReport)

		verify(mockNotifier, never()).notify(any())
	}

	@Test
	fun whenSubscribedAndShouldNotNotifyOnNotifiedIsNotCalled() {
		whenever(mockDecider.shouldNotify(mockReport)).thenReturn(false)

		impl()
		relay.accept(mockReport)

		verify(mockDecider, never()).onNotified(any())
	}
}