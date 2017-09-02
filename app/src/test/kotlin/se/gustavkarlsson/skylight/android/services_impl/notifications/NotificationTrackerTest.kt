package se.gustavkarlsson.skylight.android.services_impl.notifications

import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.Settings
import se.gustavkarlsson.skylight.android.services.SingletonCache
import se.gustavkarlsson.skylight.android.services.evaluation.Chance
import se.gustavkarlsson.skylight.android.services.evaluation.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.evaluation.ChanceLevel

@RunWith(MockitoJUnitRunner::class)
class NotificationTrackerTest {

	@Mock
	lateinit var mockLastNotifiedReportCache: SingletonCache<AuroraReport>

	@Mock
	lateinit var mockChanceEvaluator: ChanceEvaluator<AuroraReport>

	@Mock
	lateinit var mockSettings: Settings

	@Mock
	lateinit var mockOutdatedEvaluator: ReportOutdatedEvaluator

    @Mock
    lateinit var mockNewAuroraReport: AuroraReport

    @Mock
    lateinit var mockLastAuroraReport: AuroraReport

	lateinit var impl: NotificationTracker

    @Before
    fun setUp() {
        whenever(mockLastNotifiedReportCache.value).thenReturn(mockLastAuroraReport)
        whenever(mockChanceEvaluator.evaluate(mockLastAuroraReport)).thenReturn(Chance(0.5))
        whenever(mockChanceEvaluator.evaluate(mockNewAuroraReport)).thenReturn(Chance(0.9))
        whenever(mockSettings.isEnableNotifications).thenReturn(true)
        whenever(mockSettings.triggerLevel).thenReturn(ChanceLevel.MEDIUM)

        impl = NotificationTracker(mockLastNotifiedReportCache, mockChanceEvaluator, mockSettings, mockOutdatedEvaluator)
    }

    @Test
    fun notifyIfAllThingsAreFulfilled() {
        val shouldNotify = impl.shouldNotify(mockNewAuroraReport)

		assertThat(shouldNotify).isEqualTo(true)
    }

    @Test
    fun dontNotifyIfNewChanceIsLower() {
        whenever(mockSettings.triggerLevel).thenReturn(ChanceLevel.LOW)
		whenever(mockChanceEvaluator.evaluate(mockNewAuroraReport)).thenReturn(Chance(0.2))

        val shouldNotify = impl.shouldNotify(mockNewAuroraReport)

        assertThat(shouldNotify).isEqualTo(false)
    }

    @Test
    fun dontNotifyIfBothChancesAreTheSame() {
        whenever(mockChanceEvaluator.evaluate(mockLastAuroraReport)).thenReturn(Chance(0.5))
        whenever(mockChanceEvaluator.evaluate(mockNewAuroraReport)).thenReturn(Chance(0.5))

        val shouldNotify = impl.shouldNotify(mockNewAuroraReport)

        assertThat(shouldNotify).isEqualTo(false)
    }

    @Test
    fun dontNotifyIfNotificationsAreDisabled() {
        whenever(mockSettings.isEnableNotifications).thenReturn(false)

        val shouldNotify = impl.shouldNotify(mockNewAuroraReport)

        assertThat(shouldNotify).isEqualTo(false)
    }

    @Test
    fun dontNotifyIfTriggerLevelIsHigherThanNew() {
        whenever(mockChanceEvaluator.evaluate(mockNewAuroraReport)).thenReturn(Chance(0.5))
        whenever(mockSettings.triggerLevel).thenReturn(ChanceLevel.HIGH)

        val shouldNotify = impl.shouldNotify(mockNewAuroraReport)

        assertThat(shouldNotify).isEqualTo(false)
    }
}
