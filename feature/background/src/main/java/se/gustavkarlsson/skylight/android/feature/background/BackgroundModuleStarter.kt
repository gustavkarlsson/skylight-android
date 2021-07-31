package se.gustavkarlsson.skylight.android.feature.background

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import se.gustavkarlsson.skylight.android.core.ModuleStarter
import se.gustavkarlsson.skylight.android.feature.background.notifications.NotificationChannelCreator
import se.gustavkarlsson.skylight.android.feature.background.scheduling.Scheduler
import se.gustavkarlsson.skylight.android.lib.settings.SettingsRepository
import java.io.File

internal class BackgroundModuleStarter(
    private val context: Context,
    private val scheduler: Scheduler,
    private val settingsRepository: SettingsRepository,
    private val notificationChannelCreator: NotificationChannelCreator,
    private val globalScope: CoroutineScope,
) : ModuleStarter {
    override suspend fun start() {
        deleteOldNotifiedPrefsFile()
        globalScope.launch {
            notificationChannelCreator.createChannel()
            scheduleBasedOnSettings()
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
