package se.gustavkarlsson.skylight.android.services_impl.notifications

import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verifyZeroInteractions
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
        whenever(mockSettings.isEnableNotifications).thenReturn(true)
        whenever(mockOutdatedEvaluator.isOutdated(mockLastAuroraReport)).thenReturn(false)

        impl = NotificationTracker(mockLastNotifiedReportCache, mockChanceEvaluator, mockSettings, mockOutdatedEvaluator)
    }

    @Test
    fun notifyIfNewChanceIsHigherThanOldChanceAndTriggerLevel() {
        whenever(mockChanceEvaluator.evaluate(mockLastAuroraReport)).thenReturn(Chance(0.2))
        whenever(mockChanceEvaluator.evaluate(mockNewAuroraReport)).thenReturn(Chance(0.9))
        whenever(mockSettings.triggerLevel).thenReturn(ChanceLevel.MEDIUM)

        val shouldNotify = impl.shouldNotify(mockNewAuroraReport)

		assertThat(shouldNotify).isEqualTo(true)
    }

    @Test
    fun notifyIfNewChanceIsSameAsOldChanceButOldChanceIsOutdated() {
        whenever(mockChanceEvaluator.evaluate(mockLastAuroraReport)).thenReturn(Chance(0.5))
        whenever(mockChanceEvaluator.evaluate(mockNewAuroraReport)).thenReturn(Chance(0.5))
        whenever(mockSettings.triggerLevel).thenReturn(ChanceLevel.LOW)
        whenever(mockOutdatedEvaluator.isOutdated(mockLastAuroraReport)).thenReturn(true)

        val shouldNotify = impl.shouldNotify(mockNewAuroraReport)

        assertThat(shouldNotify).isEqualTo(true)
    }

    @Test
    fun dontNotifyIfNotificationsAreDisabled() {
        whenever(mockSettings.isEnableNotifications).thenReturn(false)

        val shouldNotify = impl.shouldNotify(mockNewAuroraReport)

        verify(mockSettings).isEnableNotifications
		verifyNoMoreInteractions(mockSettings)
		verifyZeroInteractions(mockChanceEvaluator)
        verifyZeroInteractions(mockOutdatedEvaluator)
        assertThat(shouldNotify).isEqualTo(false)
    }

    @Test
    fun dontNotifyIfNewChanceIsLowerThanTriggerLevel() {
        whenever(mockChanceEvaluator.evaluate(mockNewAuroraReport)).thenReturn(Chance(0.5))
        whenever(mockSettings.triggerLevel).thenReturn(ChanceLevel.HIGH)

        val shouldNotify = impl.shouldNotify(mockNewAuroraReport)

		verify(mockChanceEvaluator, never()).evaluate(mockLastAuroraReport)
        assertThat(shouldNotify).isEqualTo(false)
    }

    @Test
    fun dontNotifyIfNewChanceIsLowerThanOldChance() {
        whenever(mockChanceEvaluator.evaluate(mockLastAuroraReport)).thenReturn(Chance(0.5))
        whenever(mockChanceEvaluator.evaluate(mockNewAuroraReport)).thenReturn(Chance(0.2))
        whenever(mockSettings.triggerLevel).thenReturn(ChanceLevel.LOW)

        val shouldNotify = impl.shouldNotify(mockNewAuroraReport)

        assertThat(shouldNotify).isEqualTo(false)
    }

    @Test
    fun dontNotifyIfNewChanceIsSameAsOldChance() {
        whenever(mockChanceEvaluator.evaluate(mockLastAuroraReport)).thenReturn(Chance(0.5))
        whenever(mockChanceEvaluator.evaluate(mockNewAuroraReport)).thenReturn(Chance(0.5))
        whenever(mockSettings.triggerLevel).thenReturn(ChanceLevel.LOW)

        val shouldNotify = impl.shouldNotify(mockNewAuroraReport)

        assertThat(shouldNotify).isEqualTo(false)
    }
}
