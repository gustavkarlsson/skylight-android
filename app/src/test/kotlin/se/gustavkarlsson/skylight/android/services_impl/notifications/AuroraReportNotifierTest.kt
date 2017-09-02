package se.gustavkarlsson.skylight.android.services_impl.notifications

import android.app.NotificationManager
import android.content.Context
import com.nhaarman.mockito_kotlin.mock
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.mockito.any
import se.gustavkarlsson.skylight.android.services.evaluation.Chance
import se.gustavkarlsson.skylight.android.services.evaluation.ChanceEvaluator

@RunWith(RobolectricTestRunner::class)
class AuroraReportNotifierTest {

    lateinit var context: Context

	lateinit var mockNotificationManager: NotificationManager

	lateinit var mockChanceEvaluator: ChanceEvaluator<AuroraReport>

    lateinit var mockAuroraReport: AuroraReport

	lateinit var impl: AuroraReportNotifier

    @Before
    fun setUp() {
        context = RuntimeEnvironment.application
		mockNotificationManager = mock()
		mockChanceEvaluator = mock()
        mockAuroraReport = mock()
        `when`(mockChanceEvaluator.evaluate(any())).thenReturn(Chance(0.5))
        impl = AuroraReportNotifier(context, mockNotificationManager, mockChanceEvaluator)
    }

    @Test
    fun notifySendsNotification() {
        impl.notify(mockAuroraReport)

        verify(mockNotificationManager).notify(anyInt(), any())
    }
}

