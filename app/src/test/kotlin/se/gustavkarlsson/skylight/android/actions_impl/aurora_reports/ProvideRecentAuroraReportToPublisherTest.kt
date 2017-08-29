package se.gustavkarlsson.skylight.android.actions_impl.aurora_reports

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
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
    lateinit var lastProvider: AuroraReportProvider

    @Mock
    lateinit var newProvider: AuroraReportProvider

    @Mock
    lateinit var publisher: StreamPublisher<AuroraReport>

    @Mock
    lateinit var errorPublisher: StreamPublisher<UserFriendlyException>

    private val fiveSeconds = Duration.ofSeconds(5)

    @Mock
    lateinit var clock: Clock

    private val lastAuroraReport = AuroraReport(Instant.EPOCH, null, AuroraFactors(GeomagActivity(), GeomagLocation(), Darkness(), Visibility()))

    private val newAuroraReport = AuroraReport(Instant.ofEpochSecond(10), null, AuroraFactors(GeomagActivity(), GeomagLocation(), Darkness(), Visibility()))

	lateinit var impl: ProvideRecentAuroraReportToPublisher

    @Before
    fun setUp() {
        `when`(lastProvider.get()).thenReturn(lastAuroraReport)
        `when`(newProvider.get()).thenReturn(newAuroraReport)
        impl = ProvideRecentAuroraReportToPublisher(lastProvider, newProvider, publisher, errorPublisher, fiveSeconds, clock)
    }

    @Test(expected = IllegalArgumentException::class)
    fun createWithNegativeMaxAgeThrowsException() {
        ProvideRecentAuroraReportToPublisher(lastProvider, newProvider, publisher, errorPublisher, Duration.ofSeconds(-1), clock)
    }

    @Test
    fun createWithZeroMaxAgeIsOk() {
        ProvideRecentAuroraReportToPublisher(lastProvider, newProvider, publisher, errorPublisher, Duration.ZERO, clock)
    }

    @Test
    fun lastIsStillNewPublishesLast() {
		`when`(clock.instant()).thenReturn(Instant.EPOCH)

		impl()

		verify(publisher).publish(lastAuroraReport)
		verifyZeroInteractions(errorPublisher)
    }

    @Test
    fun lastIsOutdatedPublishesNew() {
        `when`(clock.instant()).thenReturn(Instant.ofEpochSecond(10))

        impl()

        verify(publisher).publish(newAuroraReport)
        verifyZeroInteractions(errorPublisher)
    }
}
