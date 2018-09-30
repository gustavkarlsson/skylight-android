package se.gustavkarlsson.skylight.android.background.notifications

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.Chance
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.mockito.any
import se.gustavkarlsson.skylight.android.services.Analytics
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.formatters.SingleValueFormatter

@RunWith(RobolectricTestRunner::class)
internal class AuroraReportNotifierTest {

    lateinit var context: Context

	lateinit var mockNotificationManager: NotificationManager

	lateinit var mockChanceEvaluator: ChanceEvaluator<AuroraReport>

	lateinit var mockChanceLevelFormatter: SingleValueFormatter<ChanceLevel>

    lateinit var mockAuroraReport: AuroraReport

	lateinit var mockAnalytics: Analytics

	lateinit var activityClass: Class<Activity>

	lateinit var impl: AuroraReportNotifier

    @Before
    fun setUp() {
        context = RuntimeEnvironment.application
		mockNotificationManager = mock()
		mockChanceEvaluator = mock()
		mockChanceLevelFormatter = mock()
        mockAuroraReport = mock()
		mockAnalytics = mock()
		activityClass = Activity::class.java
        whenever(mockChanceEvaluator.evaluate(any())).thenReturn(Chance(0.5))
        impl = AuroraReportNotifier(
			context,
			mockNotificationManager,
			mockChanceLevelFormatter,
			mockChanceEvaluator,
			activityClass,
			"channelId",
			mockAnalytics
		)
    }

    @Test
    fun notifySendsNotification() {
        impl.notify(mockAuroraReport)

        verify(mockNotificationManager).notify(anyInt(), any())
    }
}

