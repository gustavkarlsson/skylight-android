package se.gustavkarlsson.skylight.android.feature.background

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import se.gustavkarlsson.skylight.android.core.Global
import se.gustavkarlsson.skylight.android.core.ModuleStarter
import se.gustavkarlsson.skylight.android.feature.background.notifications.NotificationChannelCreator
import se.gustavkarlsson.skylight.android.feature.background.scheduling.Scheduler
import se.gustavkarlsson.skylight.android.lib.settings.SettingsRepository
import java.io.File
import javax.inject.Inject

internal class BackgroundModuleStarter @Inject constructor(
    private val context: Context,
    private val scheduler: Scheduler,
    private val settingsRepository: SettingsRepository,
    private val notificationChannelCreator: NotificationChannelCreator,
    @Global private val scope: CoroutineScope,
) : ModuleStarter {
    override suspend fun start() {
        deleteOldNotifiedPrefsFile()
        scope.launch {
            notificationChannelCreator.createChannel()
            scheduleBasedOnSettings() // TODO scheduling can be tied to activity lifecycle instead
        }
    }

    private fun deleteOldNotifiedPrefsFile() {
        val file = File(context.applicationInfo.dataDir, "shared_prefs/notified_chance.xml")
        file.delete()
    }

    private suspend fun scheduleBasedOnSettings() {
        settingsRepository.stream()
            .map { settings ->
                settings.placeIdsWithNotification.isNotEmpty()
            }
            .distinctUntilChanged()
            .collect { enable ->
                if (enable) {
                    scheduler.schedule()
                } else {
                    scheduler.unschedule()
                }
            }
    }
}
