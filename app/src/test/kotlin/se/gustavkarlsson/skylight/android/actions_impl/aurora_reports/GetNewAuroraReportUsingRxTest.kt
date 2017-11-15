package se.gustavkarlsson.skylight.android.actions_impl.aurora_reports

import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import io.reactivex.functions.Consumer
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyZeroInteractions
import org.mockito.junit.MockitoJUnitRunner
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.mockito.any
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.util.UserFriendlyException

@RunWith(MockitoJUnitRunner::class)
class GetNewAuroraReportUsingRxTest {

	@Mock
	lateinit var mockProvider: AuroraReportProvider

	@Mock
	lateinit var mockConsumer: Consumer<AuroraReport>

	@Mock
	lateinit var mockErrorConsumer: Consumer<UserFriendlyException>

	lateinit var impl: GetNewAuroraReportUsingRx

	@Before
	fun setUp() {
		impl = GetNewAuroraReportUsingRx(mockProvider, mockConsumer, mockErrorConsumer)
	}

	@Test
	fun invokeWithoutSubscribingDoesNotPublishAnything() {
		whenever(mockProvider.get()).thenReturn(Single.just(AuroraReport.empty))

		impl()

		verifyZeroInteractions(mockConsumer)
		verifyZeroInteractions(mockErrorConsumer)
	}

	@Test
	fun invokeWithSingleSubscriberPublishes() {
		whenever(mockProvider.get()).thenReturn(Single.just(AuroraReport.empty))

		impl().blockingGet()

		verify(mockConsumer).accept(AuroraReport.empty)
		verifyZeroInteractions(mockErrorConsumer)
	}

	@Test
	fun invokeWithSingleErrorPublishesError() {
		whenever(mockProvider.get()).thenReturn(Single.error(RuntimeException()))

		impl().blockingGet()

		verify(mockErrorConsumer).accept(any())
		verifyZeroInteractions(mockConsumer)
	}
}
