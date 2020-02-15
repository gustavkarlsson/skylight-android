package se.gustavkarlsson.skylight.android.feature.background.scheduling

import com.evernote.android.job.Job
import com.evernote.android.job.Job.Result.FAILURE
import com.evernote.android.job.Job.Result.SUCCESS
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.rxkotlin.toObservable
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.entities.CompleteAuroraReport
import se.gustavkarlsson.skylight.android.entities.LocationResult
import se.gustavkarlsson.skylight.android.entities.Place
import se.gustavkarlsson.skylight.android.entities.TriggerLevel
import se.gustavkarlsson.skylight.android.extensions.mapNotNull
import se.gustavkarlsson.skylight.android.feature.background.notifications.Notification
import se.gustavkarlsson.skylight.android.feature.background.notifications.PlaceWithChance
import se.gustavkarlsson.skylight.android.feature.background.notifications.AppVisibilityEvaluator
import se.gustavkarlsson.skylight.android.feature.background.notifications.NotificationEvaluator
import se.gustavkarlsson.skylight.android.feature.background.notifications.Notifier
import se.gustavkarlsson.skylight.android.services.AuroraReportProvider
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.LocationProvider
import se.gustavkarlsson.skylight.android.services.Settings
import se.gustavkarlsson.skylight.android.services.Time
import timber.log.Timber
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

// FIXME replace with workmanager
internal class NotifyJob(
	private val settings: Settings,
	private val appVisibilityEvaluator: AppVisibilityEvaluator,
	private val locationProvider: LocationProvider,
	private val reportProvider: AuroraReportProvider,
	private val chanceEvaluator: ChanceEvaluator<CompleteAuroraReport>,
	private val evaluator: NotificationEvaluator,
	private val notifier: Notifier,
	private val time: Time,
	private val timeout: Duration
) : Job() {

	override fun onRunJob(params: Params): Result {
		try {
			if (appVisibilityEvaluator.isVisible()) return SUCCESS
			val notificationData = getNotificationData()
			if (evaluator.shouldNotify(notificationData)) {
				notifier.notify(notificationData)
				evaluator.onNotified(notificationData)
			}
			return SUCCESS
		} catch (e: Exception) {
			Timber.e(e, "Failed to run job")
			return FAILURE
		}
	}

	private fun getNotificationData(): Notification {
		val placesWithChance = getPlacesToCheck()
			.concatMapMaybe { (place, triggerLevel) ->
				getPlaceWithChance(place)
					.skipOnTimeout()
					.filter { it.chanceLevel isGreaterOrEqual triggerLevel }
			}
			.blockingIterable()
			.toList()
		return Notification(
			placesWithChance,
			time.now()
		)
	}

	private fun getPlacesToCheck() =
		settings.streamNotificationTriggerLevels()
			.firstOrError()
			.flatMapObservable { it.toObservable() }
			.mapNotNull(::onlyEnabled)

	private fun onlyEnabled(
		levels: Pair<Place, TriggerLevel>
	): PlaceWithTriggerLevel? {
		val (place, triggerLevel) = levels
		return if (triggerLevel == TriggerLevel.NEVER) null
		else PlaceWithTriggerLevel(place, triggerLevel)
	}

	private fun getPlaceWithChance(place: Place) =
		reportProvider.get(getLocation(place))
			.map { report ->
				val chance = chanceEvaluator.evaluate(report)
				val chanceLevel = ChanceLevel.fromChance(chance)
				PlaceWithChance(
					place,
					chanceLevel
				)
			}

	private fun getLocation(place: Place): Single<LocationResult> =
		when (place) {
			Place.Current -> locationProvider.get()
			is Place.Custom -> Single.just(LocationResult.Success(place.location))
		}

	private fun <T : Any> Single<T>.skipOnTimeout() =
		toMaybe()
			.timeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
			.onErrorResumeNext { t: Throwable ->
				if (t is TimeoutException) {
					Timber.w(t, "Timeout")
					Maybe.empty()
				} else Maybe.error(t)
			}

	companion object {
		const val NOTIFY_JOB_TAG = "NOTIFY_JOB"
	}
}

private data class PlaceWithTriggerLevel(val place: Place, val triggerLevel: TriggerLevel)
