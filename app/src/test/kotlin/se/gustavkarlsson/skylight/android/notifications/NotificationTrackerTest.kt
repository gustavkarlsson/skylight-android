package se.gustavkarlsson.skylight.android.notifications

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
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
        `when`(settings.isEnableNotifications).thenReturn(true)
        `when`(settings.triggerLevel).thenReturn(ChanceLevel.HIGH)
        `when`(auroraChanceEvaluator.evaluate(any())).thenReturn(Chance(1.0))
        `when`(reportOutdatedEvaluator.isOutdated(any())).thenReturn(true)
    }

    @Test
    fun maxChanceShouldNotify() {
        `when`(settings.triggerLevel).thenReturn(ChanceLevel.HIGH)

        val shouldNotify = notificationTracker.shouldNotify(report)

        assertThat(shouldNotify).isTrue()
    }

    @Test
    fun mediumChanceShouldNotifyIfTriggerLevelIsLow() {
        `when`(settings.triggerLevel).thenReturn(ChanceLevel.LOW)
        `when`(auroraChanceEvaluator.evaluate(report)).thenReturn(Chance(0.5))

        val shouldNotify = notificationTracker.shouldNotify(report)

        assertThat(shouldNotify).isTrue()
    }

    @Test
    fun mediumChanceShouldNotNotifyIfTriggerLevelIsHigh() {
        `when`(settings.triggerLevel).thenReturn(ChanceLevel.HIGH)
        `when`(auroraChanceEvaluator.evaluate(report)).thenReturn(Chance(0.5))

        val shouldNotify = notificationTracker.shouldNotify(report)

        assertThat(shouldNotify).isFalse()
    }

    @Test
    fun noChanceShouldNotNotify() {
        `when`(auroraChanceEvaluator.evaluate(report)).thenReturn(Chance(0.0))

        val shouldNotify = notificationTracker.shouldNotify(report)

        assertThat(shouldNotify).isFalse()
    }

    @Test
    fun notificationsTurnedOffChanceShouldNotNotify() {
        `when`(settings.isEnableNotifications).thenReturn(false)

        val shouldNotify = notificationTracker.shouldNotify(report)

        assertThat(shouldNotify).isFalse()
    }

    @Test
    fun alreadyNotifiedAtSameLevelShouldNotNotify() {
        `when`(lastNotifiedCache.value).thenReturn(lastReport)
        `when`(reportOutdatedEvaluator.isOutdated(lastReport)).thenReturn(false)

        val shouldNotify = notificationTracker.shouldNotify(report)

        assertThat(shouldNotify).isFalse()
    }

    @Test
    fun alreadyNotifiedAtLowerLevelShouldNotify() {
        `when`(lastNotifiedCache.value).thenReturn(lastReport)
        `when`(reportOutdatedEvaluator.isOutdated(lastReport)).thenReturn(false)
        `when`(auroraChanceEvaluator.evaluate(lastReport)).thenReturn(Chance(0.5))

        val shouldNotify = notificationTracker.shouldNotify(report)

        assertThat(shouldNotify).isTrue()
    }

}
