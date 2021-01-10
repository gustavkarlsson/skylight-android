package se.gustavkarlsson.skylight.android.feature.background.scheduling

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.toList
import se.gustavkarlsson.skylight.android.core.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.core.entities.TriggerLevel
import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.feature.background.notifications.AppVisibilityEvaluator
import se.gustavkarlsson.skylight.android.feature.background.notifications.Notification
import se.gustavkarlsson.skylight.android.feature.background.notifications.NotificationEvaluator
import se.gustavkarlsson.skylight.android.feature.background.notifications.Notifier
import se.gustavkarlsson.skylight.android.feature.background.notifications.PlaceWithChance
import se.gustavkarlsson.skylight.android.lib.aurora.AuroraReportProvider
import se.gustavkarlsson.skylight.android.lib.aurora.CompleteAuroraReport
import se.gustavkarlsson.skylight.android.lib.location.LocationProvider
import se.gustavkarlsson.skylight.android.lib.location.LocationResult
import se.gustavkarlsson.skylight.android.lib.places.Place
import se.gustavkarlsson.skylight.android.lib.settings.Settings
import se.gustavkarlsson.skylight.android.lib.time.Time

internal class BackgroundWorkImpl(
    private val settings: Settings,
    private val appVisibilityEvaluator: AppVisibilityEvaluator,
    private val locationProvider: LocationProvider,
    private val reportProvider: AuroraReportProvider,
    private val notificationEvaluator: NotificationEvaluator,
    private val chanceEvaluator: ChanceEvaluator<CompleteAuroraReport>,
    private val notifier: Notifier,
    private val time: Time
) : BackgroundWork {
    override suspend operator fun invoke() {
        if (appVisibilityEvaluator.isVisible()) return
        val notificationData = getNotificationData()
        if (notificationEvaluator.shouldNotify(notificationData)) {
            notifier.notify(notificationData)
            notificationEvaluator.onNotified(notificationData)
        }
    }

    private suspend fun getNotificationData(): Notification {
        val placesWithChance = getPlacesToCheck()
            .mapNotNull { (place, triggerLevel) ->
                getPlaceWithChance(place).takeIf {
                    it.chanceLevel isGreaterOrEqual triggerLevel
                }
            }
            .toList()

        return Notification(
            placesWithChance,
            time.now()
        )
    }

    private suspend fun getPlacesToCheck(): Flow<PlaceWithTriggerLevel> {
        val triggerLevels = settings.streamNotificationTriggerLevels()
            .first()
        return triggerLevels.asFlow()
            .mapNotNull { onlyEnabled(it) }
    }

    private fun onlyEnabled(
        levels: Pair<Place, TriggerLevel>
    ): PlaceWithTriggerLevel? {
        val (place, triggerLevel) = levels
        return if (triggerLevel == TriggerLevel.NEVER) null
        else PlaceWithTriggerLevel(place, triggerLevel)
    }

    private suspend fun getPlaceWithChance(place: Place): PlaceWithChance {
        val report = reportProvider.get { getLocation(place) }
        val chance = chanceEvaluator.evaluate(report)
        val chanceLevel = ChanceLevel.fromChance(chance)
        return PlaceWithChance(place, chanceLevel)
    }

    private suspend fun getLocation(place: Place): LocationResult =
        when (place) {
            Place.Current -> locationProvider.get()
            is Place.Custom -> LocationResult.Success(place.location)
        }
}

private data class PlaceWithTriggerLevel(val place: Place, val triggerLevel: TriggerLevel)
