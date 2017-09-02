package se.gustavkarlsson.skylight.android.notifications

import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.evaluation.Chance
import se.gustavkarlsson.skylight.android.services.evaluation.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.evaluation.ChanceLevel
import se.gustavkarlsson.skylight.android.mockito.any
import se.gustavkarlsson.skylight.android.services.SingletonCache
import se.gustavkarlsson.skylight.android.services.Settings
import se.gustavkarlsson.skylight.android.services_impl.notifications.NotificationTracker
import se.gustavkarlsson.skylight.android.services_impl.notifications.ReportOutdatedEvaluator

class NotificationTrackerTest {

	@Rule
	@JvmField
	val rule = MockitoJUnit.rule()!!

    @Mock
    lateinit var lastNotifiedCache: SingletonCache<AuroraReport>

    @Mock
	lateinit var auroraChanceEvaluator: ChanceEvaluator<AuroraReport>

    @Mock
	lateinit var settings: Settings

    @Mock
	lateinit var reportOutdatedEvaluator: ReportOutdatedEvaluator

	@Mock
	lateinit var report: AuroraReport

	@Mock
	lateinit var lastReport: AuroraReport

	lateinit var notificationTracker: NotificationTracker

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        notificationTracker = NotificationTracker(lastNotifiedCache, auroraChanceEvaluator, settings, reportOutdatedEvaluator)
        whenever(settings.isEnableNotifications).thenReturn(true)
        whenever(settings.triggerLevel).thenReturn(ChanceLevel.HIGH)
        whenever(auroraChanceEvaluator.evaluate(any())).thenReturn(Chance(1.0))
        whenever(reportOutdatedEvaluator.isOutdated(any())).thenReturn(true)
    }

    @Test
    fun maxChanceShouldNotify() {
        whenever(settings.triggerLevel).thenReturn(ChanceLevel.HIGH)

        val shouldNotify = notificationTracker.shouldNotify(report)

        assertThat(shouldNotify).isTrue()
    }

    @Test
    fun mediumChanceShouldNotifyIfTriggerLevelIsLow() {
        whenever(settings.triggerLevel).thenReturn(ChanceLevel.LOW)
        whenever(auroraChanceEvaluator.evaluate(report)).thenReturn(Chance(0.5))

        val shouldNotify = notificationTracker.shouldNotify(report)

        assertThat(shouldNotify).isTrue()
    }

    @Test
    fun mediumChanceShouldNotNotifyIfTriggerLevelIsHigh() {
        whenever(settings.triggerLevel).thenReturn(ChanceLevel.HIGH)
        whenever(auroraChanceEvaluator.evaluate(report)).thenReturn(Chance(0.5))

        val shouldNotify = notificationTracker.shouldNotify(report)

        assertThat(shouldNotify).isFalse()
    }

    @Test
    fun noChanceShouldNotNotify() {
        whenever(auroraChanceEvaluator.evaluate(report)).thenReturn(Chance(0.0))

        val shouldNotify = notificationTracker.shouldNotify(report)

        assertThat(shouldNotify).isFalse()
    }

    @Test
    fun notificationsTurnedOffChanceShouldNotNotify() {
        whenever(settings.isEnableNotifications).thenReturn(false)

        val shouldNotify = notificationTracker.shouldNotify(report)

        assertThat(shouldNotify).isFalse()
    }

    @Test
    fun alreadyNotifiedAtSameLevelShouldNotNotify() {
        whenever(lastNotifiedCache.value).thenReturn(lastReport)
        whenever(reportOutdatedEvaluator.isOutdated(lastReport)).thenReturn(false)

        val shouldNotify = notificationTracker.shouldNotify(report)

        assertThat(shouldNotify).isFalse()
    }

    @Test
    fun alreadyNotifiedAtLowerLevelShouldNotify() {
        whenever(lastNotifiedCache.value).thenReturn(lastReport)
        whenever(reportOutdatedEvaluator.isOutdated(lastReport)).thenReturn(false)
        whenever(auroraChanceEvaluator.evaluate(lastReport)).thenReturn(Chance(0.5))

        val shouldNotify = notificationTracker.shouldNotify(report)

        assertThat(shouldNotify).isTrue()
    }

}
