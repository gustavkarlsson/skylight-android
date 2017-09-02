package se.gustavkarlsson.skylight.android.actions_impl.aurora_reports

import com.nhaarman.mockito_kotlin.whenever
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
import se.gustavkarlsson.skylight.android.services.streams.StreamPublisher
import se.gustavkarlsson.skylight.android.util.UserFriendlyException

@RunWith(MockitoJUnitRunner::class)
class ProvideNewAuroraReportToPublisherTest {

    @Mock
    lateinit var provider: AuroraReportProvider

    @Mock
    lateinit var publisher: StreamPublisher<AuroraReport>

    @Mock
    lateinit var errorPublisher: StreamPublisher<UserFriendlyException>

    lateinit var impl: ProvideNewAuroraReportToPublisher

    @Before
    fun setUp() {
        impl = ProvideNewAuroraReportToPublisher(provider, publisher, errorPublisher)
    }

    @Test
    fun invokePublishes() {
        whenever(provider.get()).thenReturn(AuroraReport.default)

        impl()

        verify(publisher).publish(AuroraReport.default)
        verifyZeroInteractions(errorPublisher)
    }

    @Test
    fun invokeWithErrorPublishesError() {
        whenever(provider.get()).thenThrow(RuntimeException())

        impl()

        verify(errorPublisher).publish(any())
        verifyZeroInteractions(publisher)
    }
}
