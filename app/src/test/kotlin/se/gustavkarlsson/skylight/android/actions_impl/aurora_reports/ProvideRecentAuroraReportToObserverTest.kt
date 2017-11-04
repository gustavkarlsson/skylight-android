package se.gustavkarlsson.skylight.android.actions_impl.aurora_reports

import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observer
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
import se.gustavkarlsson.skylight.android.util.UserFriendlyException

@RunWith(MockitoJUnitRunner::class)
class ProvideRecentAuroraReportToObserverTest {

    @Mock
    lateinit var mockLastProvider: AuroraReportProvider

    @Mock
    lateinit var mockNewProvider: AuroraReportProvider

    @Mock
    lateinit var mockObserver: Observer<AuroraReport>

    @Mock
    lateinit var mockErrorObserver: Observer<UserFriendlyException>

    @Mock
    lateinit var mockClock: Clock

    private val fiveSeconds = Duration.ofSeconds(5)

    private val lastAuroraReport = AuroraReport(Instant.EPOCH, null, AuroraFactors(KpIndex(), GeomagLocation(), Darkness(), Visibility()))

    private val newAuroraReport = AuroraReport(Instant.ofEpochSecond(10), null, AuroraFactors(KpIndex(), GeomagLocation(), Darkness(), Visibility()))

	lateinit var impl: ProvideRecentAuroraReportToObserver

    @Before
    fun setUp() {
        whenever(mockLastProvider.get()).thenReturn(lastAuroraReport)
        whenever(mockNewProvider.get()).thenReturn(newAuroraReport)
        impl = ProvideRecentAuroraReportToObserver(mockLastProvider, mockNewProvider, mockObserver, mockErrorObserver, fiveSeconds, mockClock)
    }

    @Test(expected = IllegalArgumentException::class)
    fun createWithNegativeMaxAgeThrowsException() {
        ProvideRecentAuroraReportToObserver(mockLastProvider, mockNewProvider, mockObserver, mockErrorObserver, Duration.ofSeconds(-1), mockClock)
    }

    @Test
    fun createWithZeroMaxAgeIsOk() {
        ProvideRecentAuroraReportToObserver(mockLastProvider, mockNewProvider, mockObserver, mockErrorObserver, Duration.ZERO, mockClock)
    }

    @Test
    fun lastIsStillNewPublishesLast() {
		whenever(mockClock.instant()).thenReturn(Instant.EPOCH)

		impl()

		verify(mockObserver).onNext(lastAuroraReport)
		verifyZeroInteractions(mockErrorObserver)
    }

    @Test
    fun lastIsOutdatedPublishesNew() {
        whenever(mockClock.instant()).thenReturn(Instant.ofEpochSecond(10))

        impl()

        verify(mockObserver).onNext(newAuroraReport)
        verifyZeroInteractions(mockErrorObserver)
    }
}
