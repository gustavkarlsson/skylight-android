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
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.mockito.any
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.util.UserFriendlyException

@RunWith(MockitoJUnitRunner::class)
class ProvideNewAuroraReportToObserverTest {

    @Mock
    lateinit var mockProvider: AuroraReportProvider

    @Mock
    lateinit var mockObserver: Observer<AuroraReport>

    @Mock
    lateinit var mockErrorObserver: Observer<UserFriendlyException>

    lateinit var impl: ProvideNewAuroraReportToObserver

    @Before
    fun setUp() {
        impl = ProvideNewAuroraReportToObserver(mockProvider, mockObserver, mockErrorObserver)
    }

    @Test
    fun invokePublishes() {
        whenever(mockProvider.get()).thenReturn(AuroraReport.empty)

        impl()

        verify(mockObserver).onNext(AuroraReport.empty)
        verifyZeroInteractions(mockErrorObserver)
    }

    @Test
    fun invokeWithErrorPublishesError() {
        whenever(mockProvider.get()).thenThrow(RuntimeException())

        impl()

        verify(mockErrorObserver).onNext(any())
        verifyZeroInteractions(mockObserver)
    }
}
