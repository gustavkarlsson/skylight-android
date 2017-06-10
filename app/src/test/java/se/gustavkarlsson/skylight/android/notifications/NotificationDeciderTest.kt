package se.gustavkarlsson.skylight.android.notifications

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import se.gustavkarlsson.skylight.android.cache.SingletonCache
import se.gustavkarlsson.skylight.android.evaluation.Chance
import se.gustavkarlsson.skylight.android.evaluation.ChanceEvaluator
import se.gustavkarlsson.skylight.android.evaluation.ChanceLevel
import se.gustavkarlsson.skylight.android.models.AuroraFactors
import se.gustavkarlsson.skylight.android.models.AuroraReport
import se.gustavkarlsson.skylight.android.models.factors.Darkness
import se.gustavkarlsson.skylight.android.models.factors.GeomagActivity
import se.gustavkarlsson.skylight.android.models.factors.GeomagLocation
import se.gustavkarlsson.skylight.android.models.factors.Visibility
import se.gustavkarlsson.skylight.android.settings.Settings

@RunWith(MockitoJUnitRunner::class)
class NotificationDeciderTest {

    @Mock
    lateinit var lastNotifiedCache: SingletonCache<AuroraReport>

    @Mock
	lateinit var auroraChanceEvaluator: ChanceEvaluator<AuroraReport>

    @Mock
	lateinit var settings: Settings

    @Mock
	lateinit var reportOutdatedEvaluator: ReportOutdatedEvaluator

	lateinit var notificationDecider: NotificationDecider
	lateinit var report: AuroraReport
	lateinit var lastReport: AuroraReport

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        notificationDecider = NotificationDecider(lastNotifiedCache, auroraChanceEvaluator, settings, reportOutdatedEvaluator)
        report = createDummyReport()
        lastReport = createDummyReport()
        `when`(settings.isEnableNotifications).thenReturn(true)
        `when`(settings.triggerLevel).thenReturn(ChanceLevel.HIGH)
        `when`(auroraChanceEvaluator.evaluate(any())).thenReturn(Chance(1.0))
        `when`(reportOutdatedEvaluator.isOutdated(any())).thenReturn(true)
    }

    private fun createDummyReport(): AuroraReport {
        return AuroraReport(0, null, AuroraFactors(GeomagActivity(), GeomagLocation(), Darkness(), Visibility()))
    }

    @Test
    fun maxChanceShouldNotify() {
        `when`(settings.triggerLevel).thenReturn(ChanceLevel.HIGH)

        val shouldNotify = notificationDecider.shouldNotify(report)

        assertThat(shouldNotify).isTrue()
    }

    @Test
    fun mediumChanceShouldNotifyIfTriggerLevelIsLow() {
        `when`(settings.triggerLevel).thenReturn(ChanceLevel.LOW)
        `when`(auroraChanceEvaluator.evaluate(report)).thenReturn(Chance(0.5))

        val shouldNotify = notificationDecider.shouldNotify(report)

        assertThat(shouldNotify).isTrue()
    }

    @Test
    fun mediumChanceShouldNotNotifyIfTriggerLevelIsHigh() {
        `when`(settings.triggerLevel).thenReturn(ChanceLevel.HIGH)
        `when`(auroraChanceEvaluator.evaluate(report)).thenReturn(Chance(0.5))

        val shouldNotify = notificationDecider.shouldNotify(report)

        assertThat(shouldNotify).isFalse()
    }

    @Test
    fun noChanceShouldNotNotify() {
        `when`(auroraChanceEvaluator.evaluate(report)).thenReturn(Chance(0.0))

        val shouldNotify = notificationDecider.shouldNotify(report)

        assertThat(shouldNotify).isFalse()
    }

    @Test
    fun notificationsTurnedOffChanceShouldNotNotify() {
        `when`(settings.isEnableNotifications).thenReturn(false)

        val shouldNotify = notificationDecider.shouldNotify(report)

        assertThat(shouldNotify).isFalse()
    }

    @Test
    fun alreadyNotifiedAtSameLevelShouldNotNotify() {
        `when`<AuroraReport>(lastNotifiedCache.value).thenReturn(lastReport)
        `when`(reportOutdatedEvaluator.isOutdated(lastReport)).thenReturn(false)

        val shouldNotify = notificationDecider.shouldNotify(report)

        assertThat(shouldNotify).isFalse()
    }

    @Test
    fun alreadyNotifiedAtLowerLevelShouldNotify() {
        `when`<AuroraReport>(lastNotifiedCache.value).thenReturn(lastReport)
        `when`(reportOutdatedEvaluator.isOutdated(lastReport)).thenReturn(false)
        `when`(auroraChanceEvaluator.evaluate(lastReport)).thenReturn(Chance(0.5))

        val shouldNotify = notificationDecider.shouldNotify(report)

        assertThat(shouldNotify).isTrue()
    }

	// Replacement for mockito's any() not working with kotlin
	private fun <T> any(): T {
		Mockito.any<T>()
		return null as T
	}

}
