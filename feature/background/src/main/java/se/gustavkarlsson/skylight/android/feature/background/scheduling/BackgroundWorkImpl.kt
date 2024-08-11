package se.gustavkarlsson.skylight.android.feature.background.scheduling

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import se.gustavkarlsson.skylight.android.core.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.core.utils.nonEmpty
import se.gustavkarlsson.skylight.android.feature.background.notifications.AppVisibilityEvaluator
import se.gustavkarlsson.skylight.android.feature.background.notifications.Notification
import se.gustavkarlsson.skylight.android.feature.background.notifications.NotificationEvaluator
import se.gustavkarlsson.skylight.android.feature.background.notifications.Notifier
import se.gustavkarlsson.skylight.android.feature.background.notifications.PlaceWithChance
import se.gustavkarlsson.skylight.android.lib.aurora.AuroraReport
import se.gustavkarlsson.skylight.android.lib.aurora.AuroraReportProvider
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.location.LocationProvider
import se.gustavkarlsson.skylight.android.lib.places.Place
import se.gustavkarlsson.skylight.android.lib.places.PlaceId
import se.gustavkarlsson.skylight.android.lib.places.PlacesRepository
import se.gustavkarlsson.skylight.android.lib.settings.SettingsRepository
import se.gustavkarlsson.skylight.android.lib.time.Time
import javax.inject.Inject

internal class BackgroundWorkImpl @Inject constructor(
    private val placesRepository: PlacesRepository,
    private val settingsRepository: SettingsRepository,
    private val appVisibilityEvaluator: AppVisibilityEvaluator,
    private val locationProvider: LocationProvider,
    private val reportProvider: AuroraReportProvider,
    private val notificationEvaluator: NotificationEvaluator,
    private val chanceEvaluator: ChanceEvaluator<AuroraReport>,
    private val notifier: Notifier,
    private val time: Time,
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
        val settings = settingsRepository.stream().first()
        val placesWithChance = settings.placeIdsWithNotification
            .mapNotNull { placeId ->
                getPlaceWithChance(placeId)?.takeIf {
                    it.chanceLevel isGreaterOrEqual settings.notificationTriggerLevel
                }
            }
            .sortedByDescending { it.chanceLevel }
            .nonEmpty()

        return placesWithChance.map { list ->
            Notification(list, time.now())
        }.getOrNull()
    }

    private suspend fun getPlaceWithChance(placeId: PlaceId): PlaceWithChance? {
        val places = placesRepository.stream().firstOrNull() ?: return null
        val place = places.find { place -> place.id == placeId } ?: return null
        val location = getLocation(place) ?: return null
        val report = reportProvider.get(location)
        val chance = chanceEvaluator.evaluate(report)
        val chanceLevel = ChanceLevel.fromChance(chance)
        return PlaceWithChance(place, chanceLevel)
    }

    private suspend fun getLocation(place: Place): Location? =
        when (place) {
            Place.Current -> locationProvider.get().getOrNull()
            is Place.Saved -> place.location
        }
}
