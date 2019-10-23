package se.gustavkarlsson.skylight.android.feature.background.notifications

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import com.ioki.textref.TextRef
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import se.gustavkarlsson.skylight.android.entities.CompleteAuroraReport
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

	lateinit var mockChanceEvaluator: ChanceEvaluator<CompleteAuroraReport>

	lateinit var mockChanceLevelFormatter: SingleValueFormatter<ChanceLevel>

    lateinit var mockAuroraReport: CompleteAuroraReport

	lateinit var mockAnalytics: Analytics

	lateinit var activityClass: Class<Activity>

	lateinit var impl: AuroraReportNotifier

    @Before
    fun setUp() {
        context = RuntimeEnvironment.application
		val chance = Chance(0.5)
		val chanceLevel = ChanceLevel.fromChance(chance)
		mockNotificationManager = mock()
		mockChanceEvaluator = mock()
		mockChanceLevelFormatter = mock {
			whenever(it.format(chanceLevel)).thenReturn(TextRef("some chance"))
		}
        mockAuroraReport = mock()
		mockAnalytics = mock()
		activityClass = Activity::class.java
		whenever(mockChanceEvaluator.evaluate(any())).thenReturn(chance)
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

