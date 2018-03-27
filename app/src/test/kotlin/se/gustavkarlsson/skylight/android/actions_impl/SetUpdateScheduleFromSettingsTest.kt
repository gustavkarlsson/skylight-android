package se.gustavkarlsson.skylight.android.actions_impl

import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import se.gustavkarlsson.skylight.android.services.Scheduler

@RunWith(MockitoJUnitRunner::class)
class SetUpdateScheduleFromSettingsTest {

	@Mock
	lateinit var mockSettings: Settings

    @Mock
    lateinit var mockScheduler: Scheduler

	lateinit var impl: SetUpdateScheduleFromSettings

    @Before
    fun setUp() {
        impl = SetUpdateScheduleFromSettings(mockSettings, mockScheduler)
    }

    @Test
    fun scheduleIfEnabled() {
        whenever(mockSettings.isEnableNotifications).thenReturn(true)

		impl()

		verify(mockScheduler).schedule()
    }

    @Test
    fun unscheduleIfDisabled() {
        whenever(mockSettings.isEnableNotifications).thenReturn(false)

        impl()

        verify(mockScheduler).unschedule()
    }
}
