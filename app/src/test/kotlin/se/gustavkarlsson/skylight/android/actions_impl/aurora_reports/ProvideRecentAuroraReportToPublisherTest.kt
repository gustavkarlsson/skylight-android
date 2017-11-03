package se.gustavkarlsson.skylight.android.actions_impl.aurora_reports

import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyZeroInteractions
import org.mockito.junit.MockitoJUnitRunner
import org.threeten.bp.Clock
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.services.streams.StreamPublisher
import se.gustavkarlsson.skylight.android.util.UserFriendlyException

@RunWith(MockitoJUnitRunner::class)
class ProvideRecentAuroraReportToPublisherTest {

    @Mock
    lateinit var mockLastProvider: AuroraReportProvider

    @Mock
    lateinit var mockNewProvider: AuroraReportProvider

    @Mock
    lateinit var mockPublisher: StreamPublisher<AuroraReport>

    @Mock
    lateinit var mockErrorPublisher: StreamPublisher<UserFriendlyException>

    @Mock
    lateinit var mockClock: Clock

    private val fiveSeconds = Duration.ofSeconds(5)

    private val lastAuroraReport = AuroraReport(Instant.EPOCH, null, AuroraFactors(KpIndex(), GeomagLocation(), Darkness(), Visibility()))

    private val newAuroraReport = AuroraReport(Instant.ofEpochSecond(10), null, AuroraFactors(KpIndex(), GeomagLocation(), Darkness(), Visibility()))

	lateinit var impl: ProvideRecentAuroraReportToPublisher

    @Before
    fun setUp() {
        whenever(mockLastProvider.get()).thenReturn(lastAuroraReport)
        whenever(mockNewProvider.get()).thenReturn(newAuroraReport)
        impl = ProvideRecentAuroraReportToPublisher(mockLastProvider, mockNewProvider, mockPublisher, mockErrorPublisher, fiveSeconds, mockClock)
    }

    @Test(expected = IllegalArgumentException::class)
    fun createWithNegativeMaxAgeThrowsException() {
        ProvideRecentAuroraReportToPublisher(mockLastProvider, mockNewProvider, mockPublisher, mockErrorPublisher, Duration.ofSeconds(-1), mockClock)
    }

    @Test
    fun createWithZeroMaxAgeIsOk() {
        ProvideRecentAuroraReportToPublisher(mockLastProvider, mockNewProvider, mockPublisher, mockErrorPublisher, Duration.ZERO, mockClock)
    }

    @Test
    fun lastIsStillNewPublishesLast() {
		whenever(mockClock.instant()).thenReturn(Instant.EPOCH)

		impl()

		verify(mockPublisher).publish(lastAuroraReport)
		verifyZeroInteractions(mockErrorPublisher)
    }

    @Test
    fun lastIsOutdatedPublishesNew() {
        whenever(mockClock.instant()).thenReturn(Instant.ofEpochSecond(10))

        impl()

        verify(mockPublisher).publish(newAuroraReport)
        verifyZeroInteractions(mockErrorPublisher)
    }
}
