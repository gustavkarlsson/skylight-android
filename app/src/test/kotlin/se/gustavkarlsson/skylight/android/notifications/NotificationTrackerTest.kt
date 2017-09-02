package se.gustavkarlsson.skylight.android.notifications

import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.mockito.any
import se.gustavkarlsson.skylight.android.services.Settings
import se.gustavkarlsson.skylight.android.services.SingletonCache
import se.gustavkarlsson.skylight.android.services.evaluation.Chance
import se.gustavkarlsson.skylight.android.services.evaluation.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.evaluation.ChanceLevel
import se.gustavkarlsson.skylight.android.services_impl.notifications.NotificationTracker
import se.gustavkarlsson.skylight.android.services_impl.notifications.ReportOutdatedEvaluator

@RunWith(MockitoJUnitRunner::class)
class NotificationTrackerTest {

    @Mock
    lateinit var mockLastNotifiedCache: SingletonCache<AuroraReport>

    @Mock
    lateinit var mockAuroraChanceEvaluator: ChanceEvaluator<AuroraReport>

    @Mock
    lateinit var mockSettings: Settings

    @Mock
    lateinit var mockReportOutdatedEvaluator: ReportOutdatedEvaluator

    @Mock
    lateinit var mockReport: AuroraReport

    @Mock
    lateinit var mockLastReport: AuroraReport

    lateinit var impl: NotificationTracker

    @Before
    fun setUp() {
        impl = NotificationTracker(mockLastNotifiedCache, mockAuroraChanceEvaluator, mockSettings, mockReportOutdatedEvaluator)
        whenever(mockSettings.isEnableNotifications).thenReturn(true)
        whenever(mockSettings.triggerLevel).thenReturn(ChanceLevel.HIGH)
        whenever(mockAuroraChanceEvaluator.evaluate(any())).thenReturn(Chance(1.0))
        whenever(mockReportOutdatedEvaluator.isOutdated(any())).thenReturn(true)
    }

    @Test
    fun maxChanceShouldNotify() {
        whenever(mockSettings.triggerLevel).thenReturn(ChanceLevel.HIGH)

        val shouldNotify = impl.shouldNotify(mockReport)

        assertThat(shouldNotify).isTrue()
    }

    @Test
    fun mediumChanceShouldNotifyIfTriggerLevelIsLow() {
        whenever(mockSettings.triggerLevel).thenReturn(ChanceLevel.LOW)
        whenever(mockAuroraChanceEvaluator.evaluate(mockReport)).thenReturn(Chance(0.5))

        val shouldNotify = impl.shouldNotify(mockReport)

        assertThat(shouldNotify).isTrue()
    }

    @Test
    fun mediumChanceShouldNotNotifyIfTriggerLevelIsHigh() {
        whenever(mockSettings.triggerLevel).thenReturn(ChanceLevel.HIGH)
        whenever(mockAuroraChanceEvaluator.evaluate(mockReport)).thenReturn(Chance(0.5))

        val shouldNotify = impl.shouldNotify(mockReport)

        assertThat(shouldNotify).isFalse()
    }

    @Test
    fun noChanceShouldNotNotify() {
        whenever(mockAuroraChanceEvaluator.evaluate(mockReport)).thenReturn(Chance(0.0))

        val shouldNotify = impl.shouldNotify(mockReport)

        assertThat(shouldNotify).isFalse()
    }

    @Test
    fun notificationsTurnedOffChanceShouldNotNotify() {
        whenever(mockSettings.isEnableNotifications).thenReturn(false)

        val shouldNotify = impl.shouldNotify(mockReport)

        assertThat(shouldNotify).isFalse()
    }

    @Test
    fun alreadyNotifiedAtSameLevelShouldNotNotify() {
        whenever(mockLastNotifiedCache.value).thenReturn(mockLastReport)
        whenever(mockReportOutdatedEvaluator.isOutdated(mockLastReport)).thenReturn(false)

        val shouldNotify = impl.shouldNotify(mockReport)

        assertThat(shouldNotify).isFalse()
    }

    @Test
    fun alreadyNotifiedAtLowerLevelShouldNotify() {
        whenever(mockLastNotifiedCache.value).thenReturn(mockLastReport)
        whenever(mockReportOutdatedEvaluator.isOutdated(mockLastReport)).thenReturn(false)
        whenever(mockAuroraChanceEvaluator.evaluate(mockLastReport)).thenReturn(Chance(0.5))

        val shouldNotify = impl.shouldNotify(mockReport)

        assertThat(shouldNotify).isTrue()
    }

}
