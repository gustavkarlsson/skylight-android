package se.gustavkarlsson.skylight.android.feature.background.scheduling

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.rxkotlin.toObservable
import kotlinx.coroutines.rx2.rxSingle
import se.gustavkarlsson.skylight.android.core.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.core.entities.TriggerLevel
import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.core.utils.mapNotNull
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

internal fun createNotifyWork(
    settings: Settings,
    appVisibilityEvaluator: AppVisibilityEvaluator,
    locationProvider: LocationProvider,
    reportProvider: AuroraReportProvider,
    chanceEvaluator: ChanceEvaluator<CompleteAuroraReport>,
    notificationEvaluator: NotificationEvaluator,
    notifier: Notifier,
    time: Time
): Completable {

    fun getLocation(place: Place): Single<LocationResult> =
        when (place) {
            Place.Current -> rxSingle { locationProvider.get() }
            is Place.Custom -> Single.just(LocationResult.Success(place.location))
        }

    fun onlyEnabled(
        levels: Pair<Place, TriggerLevel>
    ): PlaceWithTriggerLevel? {
        val (place, triggerLevel) = levels
        return if (triggerLevel == TriggerLevel.NEVER) null
        else PlaceWithTriggerLevel(place, triggerLevel)
    }

    fun getPlacesToCheck() =
        settings.streamNotificationTriggerLevels()
            .firstOrError()
            .flatMapObservable { it.toObservable() }
            .mapNotNull(::onlyEnabled)

    fun getPlaceWithChance(place: Place) =
        reportProvider.get(getLocation(place))
            .map { report ->
                val chance = chanceEvaluator.evaluate(report)
                val chanceLevel = ChanceLevel.fromChance(chance)
                PlaceWithChance(
                    place,
                    chanceLevel
                )
            }

    fun getNotificationData(): Notification {
        val placesWithChance = getPlacesToCheck()
            .concatMapMaybe { (place, triggerLevel) ->
                getPlaceWithChance(place)
                    .filter { it.chanceLevel isGreaterOrEqual triggerLevel }
            }
            .blockingIterable()
            .toList()
        return Notification(
            placesWithChance,
            time.now()
        )
    }

    return Completable.defer {
        if (appVisibilityEvaluator.isVisible()) Completable.complete()
        else Single.fromCallable(::getNotificationData)
            .doOnSuccess { notificationData ->
                if (notificationEvaluator.shouldNotify(notificationData)) {
                    notifier.notify(notificationData)
                    notificationEvaluator.onNotified(notificationData)
                }
            }
            .ignoreElement()
    }
}

private data class PlaceWithTriggerLevel(val place: Place, val triggerLevel: TriggerLevel)
