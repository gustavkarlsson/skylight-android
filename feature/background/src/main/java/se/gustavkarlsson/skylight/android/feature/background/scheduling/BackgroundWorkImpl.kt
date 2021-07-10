package se.gustavkarlsson.skylight.android.feature.background.scheduling

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
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
import se.gustavkarlsson.skylight.android.lib.places.PlaceId
import se.gustavkarlsson.skylight.android.lib.places.PlacesRepository
import se.gustavkarlsson.skylight.android.lib.settings.Settings
import se.gustavkarlsson.skylight.android.lib.time.Time

internal class BackgroundWorkImpl(
    private val placesRepository: PlacesRepository,
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
        val notificationData = getNotificationData() ?: return
        if (notificationEvaluator.shouldNotify(notificationData)) {
            notifier.notify(notificationData)
            notificationEvaluator.onNotified(notificationData)
        }
    }

    private suspend fun getNotificationData(): Notification? {
        val placesWithChance = getPlaceIdsToCheck()
            .mapNotNull { (placeId, triggerLevel) ->
                getPlaceWithChance(placeId)?.takeIf {
                    it.chanceLevel isGreaterOrEqual triggerLevel
                }
            }
            .sortedByDescending { it.chanceLevel }

        return if (placesWithChance.isNotEmpty()) {
            val targetPlace = placesWithChance.first().place
            Notification(targetPlace, placesWithChance, time.now())
        } else null
    }

    private suspend fun getPlaceIdsToCheck(): List<PlaceIdWithTriggerLevel> {
        val triggerLevels = settings.streamNotificationTriggerLevels()
            .first()
        return triggerLevels.asMap()
            .map { (placeId, triggerLevel) ->
                PlaceIdWithTriggerLevel(placeId, triggerLevel)
            }
            .filter { it.enabled }
    }

    private val PlaceIdWithTriggerLevel.enabled: Boolean
        get() = triggerLevel != TriggerLevel.NEVER

    private suspend fun getPlaceWithChance(placeId: PlaceId): PlaceWithChance? {
        val places = placesRepository.stream().firstOrNull() ?: return null
        val place = places.find { place -> place.id == placeId } ?: return null
        val report = reportProvider.get { getLocation(place) }
        val chance = chanceEvaluator.evaluate(report)
        val chanceLevel = ChanceLevel.fromChance(chance)
        return PlaceWithChance(place, chanceLevel)
    }

    private suspend fun getLocation(place: Place): LocationResult =
        when (place) {
            Place.Current -> locationProvider.get()
            is Place.Saved -> LocationResult.Success(place.location)
        }
}

private data class PlaceIdWithTriggerLevel(val id: PlaceId, val triggerLevel: TriggerLevel)
