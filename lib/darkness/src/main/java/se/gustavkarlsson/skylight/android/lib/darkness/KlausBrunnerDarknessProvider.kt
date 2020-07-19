package se.gustavkarlsson.skylight.android.lib.darkness

import com.jakewharton.rx.replayingShare
import io.reactivex.Observable
import io.reactivex.Single
import java.util.GregorianCalendar
import net.e175.klaus.solarpositioning.Grena3
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.core.entities.Cause
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.entities.Report
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.core.utils.delay
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.location.LocationResult
import se.gustavkarlsson.skylight.android.lib.time.Time
internal class KlausBrunnerDarknessProvider(
    private val time: Time,
    private val pollingInterval: Duration
) : DarknessProvider {

    override fun get(location: Single<LocationResult>): Single<Report<Darkness>> =
        location
            .map { getDarkness(it, time.now()) }
            .doOnSuccess { logInfo { "Provided darkness: $it" } }

    override fun stream(
        locations: Observable<Loadable<LocationResult>>
    ): Observable<Loadable<Report<Darkness>>> =
        locations
            .switchMap { loadableLocation ->
                when (loadableLocation) {
                    Loadable.Loading -> Observable.just(Loadable.Loading)
                    is Loadable.Loaded -> {
                        Single
                            .fromCallable {
                                getDarkness(loadableLocation.value, time.now())
                            }
                            .map { Loadable.Loaded(it) }
                            .repeatWhen { it.delay(pollingInterval) }
                            .toObservable()
                    }
                }
            }
            .distinctUntilChanged()
            .doOnNext { logInfo { "Streamed darkness: $it" } }
            .replayingShare(Loadable.Loading)

    private fun getDarkness(locationResult: LocationResult, timestamp: Instant): Report<Darkness> =
        locationResult.map(
            onSuccess = {
                val sunZenithAngle = calculateSunZenithAngle(it, timestamp)
                Report.Success(Darkness(sunZenithAngle), timestamp)
            },
            onMissingPermissionError = {
                Report.Error(Cause.LocationPermission, timestamp)
            },
            onUnknownError = {
                Report.Error(Cause.Location, timestamp)
            }
        )
}

private fun calculateSunZenithAngle(
    location: Location,
    time: Instant
): Double {
    val date = time.toGregorianCalendar()
    val azimuthAndZenithAngle = Grena3.calculateSolarPosition(
        date,
        location.latitude,
        location.longitude,
        0.0
    )
    return azimuthAndZenithAngle.zenithAngle
}

private fun Instant.toGregorianCalendar() =
    GregorianCalendar().apply { timeInMillis = toEpochMilli() }
