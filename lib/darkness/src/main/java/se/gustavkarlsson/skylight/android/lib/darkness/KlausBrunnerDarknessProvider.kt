package se.gustavkarlsson.skylight.android.lib.darkness

import java.util.GregorianCalendar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import net.e175.klaus.solarpositioning.Grena3
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.core.entities.Cause
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.entities.Report
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.location.LocationResult
import se.gustavkarlsson.skylight.android.lib.time.Time

internal class KlausBrunnerDarknessProvider(
    private val time: Time,
    private val pollingInterval: Duration
) : DarknessProvider {

    override fun get(locationResult: LocationResult): Report<Darkness> {
        val report = getDarknessReport(locationResult, time.now())
        logInfo { "Provided darkness: $report" }
        return report
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun stream(
        locations: Flow<Loadable<LocationResult>>
    ): Flow<Loadable<Report<Darkness>>> =
        locations
            .flatMapLatest { loadableLocation ->
                when (loadableLocation) {
                    Loadable.Loading -> flowOf(Loadable.Loading)
                    is Loadable.Loaded -> pollLocation(loadableLocation.value)
                }
            }
            .distinctUntilChanged()
            .onEach { logInfo { "Streamed darkness: $it" } }

    private fun pollLocation(location: LocationResult): Flow<Loadable<Report<Darkness>>> =
        flow {
            while (true) {
                val darknessReport = getDarknessReport(location, time.now())
                emit(Loadable.loaded(darknessReport))
                delay(pollingInterval.toMillis())
            }
        }

    private fun getDarknessReport(locationResult: LocationResult, timestamp: Instant): Report<Darkness> =
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
