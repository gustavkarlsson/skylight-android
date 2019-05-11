package se.gustavkarlsson.skylight.android.background.notifications

import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.Mockito.verifyZeroInteractions
import org.mockito.junit.MockitoJUnitRunner
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.background.persistence.NotifiedChanceRepository
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.Chance
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.entities.NotifiedChance
import se.gustavkarlsson.skylight.android.krate.State
import se.gustavkarlsson.skylight.android.krate.SkylightStore
import se.gustavkarlsson.skylight.android.mockito.any
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator

@RunWith(MockitoJUnitRunner::class)
internal class AuroraReportNotificationDeciderImplTest {

	@Mock
	lateinit var mockNotifiedChanceRepository: NotifiedChanceRepository

	@Mock
	lateinit var mockChanceEvaluator: ChanceEvaluator<AuroraReport>

	@Mock
	lateinit var mockSettings: State.Settings

	@Mock
	lateinit var mockState: State

	@Mock
	lateinit var mockStore: SkylightStore

	@Mock
	lateinit var mockOutdatedEvaluator: OutdatedEvaluator

	@Mock
	lateinit var mockAppVisibilityEvaluator: AppVisibilityEvaluator

	@Mock
	lateinit var mockNewAuroraReport: AuroraReport

	@Mock
	lateinit var mockLastNotifiedChance: NotifiedChance

	lateinit var impl: AuroraReportNotificationDecider

	@Before
	fun setUp() {
		whenever(mockLastNotifiedChance.chance).thenReturn(Chance(0.5))
		whenever(mockLastNotifiedChance.timestamp).thenReturn(Instant.ofEpochMilli(50000))
		whenever(mockNotifiedChanceRepository.get()).thenReturn(mockLastNotifiedChance)
		whenever(mockSettings.notificationsEnabled).thenReturn(true)
		whenever(mockStore.currentState).thenReturn(mockState)
		whenever(mockState.settings).thenReturn(mockSettings)
		whenever(mockOutdatedEvaluator.isOutdated(any())).thenReturn(false)
		whenever(mockAppVisibilityEvaluator.isVisible()).thenReturn(false)

		impl =
			AuroraReportNotificationDeciderImpl(
				mockNotifiedChanceRepository,
				mockChanceEvaluator,
				mockStore,
				mockOutdatedEvaluator,
				mockAppVisibilityEvaluator
			)
	}

	@Test
	fun notifyIfNewChanceIsHigherThanOldChanceAndTriggerLevel() {
		whenever(mockChanceEvaluator.evaluate(any())).thenReturn(Chance(0.2))
		whenever(mockChanceEvaluator.evaluate(mockNewAuroraReport)).thenReturn(Chance(0.9))
		whenever(mockSettings.triggerLevel).thenReturn(ChanceLevel.MEDIUM)

		val shouldNotify = impl.shouldNotify(mockNewAuroraReport)

		assertk.assert(shouldNotify).isTrue()
	}

	@Test
	fun notifyIfNewChanceIsAboveTriggerLevelAndLastValueIsOutdated() {
		whenever(mockChanceEvaluator.evaluate(mockNewAuroraReport)).thenReturn(Chance(0.5))
		whenever(mockSettings.triggerLevel).thenReturn(ChanceLevel.LOW)
		whenever(mockOutdatedEvaluator.isOutdated(any())).thenReturn(true)

		val shouldNotify = impl.shouldNotify(mockNewAuroraReport)

		assertk.assert(shouldNotify).isEqualTo(true)
	}

	@Test
	fun dontNotifyIfNotificationsAreDisabled() {
		whenever(mockSettings.notificationsEnabled).thenReturn(false)

		val shouldNotify = impl.shouldNotify(mockNewAuroraReport)

		verify(mockSettings).notificationsEnabled
		verifyNoMoreInteractions(mockSettings)
		verifyZeroInteractions(mockChanceEvaluator)
		verifyZeroInteractions(mockOutdatedEvaluator)
		assertk.assert(shouldNotify).isEqualTo(false)
	}

	@Test
	fun dontNotifyIfNewChanceIsLowerThanTriggerLevel() {
		whenever(mockChanceEvaluator.evaluate(mockNewAuroraReport)).thenReturn(Chance(0.5))
		whenever(mockSettings.triggerLevel).thenReturn(ChanceLevel.HIGH)

		val shouldNotify = impl.shouldNotify(mockNewAuroraReport)

		assertk.assert(shouldNotify).isEqualTo(false)
	}

	@Test
	fun dontNotifyIfNewChanceIsLowerThanOldChance() {
		whenever(mockLastNotifiedChance.chance).thenReturn(Chance(0.5))
		whenever(mockChanceEvaluator.evaluate(mockNewAuroraReport)).thenReturn(Chance(0.2))
		whenever(mockSettings.triggerLevel).thenReturn(ChanceLevel.LOW)

		val shouldNotify = impl.shouldNotify(mockNewAuroraReport)

		assertk.assert(shouldNotify).isEqualTo(false)
	}

	@Test
	fun dontNotifyIfNewChanceIsSameAsOldChance() {
		whenever(mockLastNotifiedChance.chance).thenReturn(Chance(0.5))
		whenever(mockChanceEvaluator.evaluate(mockNewAuroraReport)).thenReturn(Chance(0.5))
		whenever(mockSettings.triggerLevel).thenReturn(ChanceLevel.LOW)

		val shouldNotify = impl.shouldNotify(mockNewAuroraReport)

		assertk.assert(shouldNotify).isEqualTo(false)
	}

	@Test
	fun dontNotifyIfAppIsVisible() {
		whenever(mockAppVisibilityEvaluator.isVisible()).thenReturn(true)

		val shouldNotify = impl.shouldNotify(mockNewAuroraReport)

		assertk.assert(shouldNotify).isEqualTo(false)
	}
}
