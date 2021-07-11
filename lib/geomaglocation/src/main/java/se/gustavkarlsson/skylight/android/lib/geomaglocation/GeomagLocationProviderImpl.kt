package se.gustavkarlsson.skylight.android.lib.geomaglocation

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.core.entities.Cause
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.entities.Report
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.lib.location.LocationError
import se.gustavkarlsson.skylight.android.lib.location.LocationResult
import se.gustavkarlsson.skylight.android.lib.time.Time
import java.lang.Math.toDegrees
import java.lang.Math.toRadians
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

internal class GeomagLocationProviderImpl(
    private val time: Time
) : GeomagLocationProvider {

    override fun get(locationResult: LocationResult): Report<GeomagLocation> {
        val report = getSingleGeomagLocation(locationResult, time.now())
        logInfo { "Provided geomag location: $report" }
        return report
    }

    override fun stream(
        locations: Flow<Loadable<LocationResult>>
    ): Flow<Loadable<Report<GeomagLocation>>> =
        locations
            .map { loadableLocation ->
                loadableLocation.map { location ->
                    getSingleGeomagLocation(location, time.now())
                }
            }
            .distinctUntilChanged()
            .onEach { logInfo { "Streamed geomag location: $it" } }

    private fun getSingleGeomagLocation(locationResult: LocationResult, timestamp: Instant): Report<GeomagLocation> =
        locationResult.fold(
            ifLeft = { error ->
                val cause = when (error) {
                    LocationError.NoPermission -> Cause.NoLocationPermission
                    LocationError.Unknown -> Cause.NoLocation
                }
                Report.Error(cause, timestamp)
            },
            ifRight = { location ->
                val geomagneticLatitude = calculateGeomagneticLatitude(
                    location.latitude,
                    location.longitude,
                )
                Report.Success(GeomagLocation(geomagneticLatitude), timestamp)
            }
        )

    // http://stackoverflow.com/a/7949249/940731
    private fun calculateGeomagneticLatitude(latitude1: Double, longitude1: Double): Double {
        val r = 1.0
        // convert first to radians
        val dLong = toRadians(MAGNETIC_NORTH_POLE_LONGITUDE)
        val dLat = toRadians(MAGNETIC_NORTH_POLE_LATITUDE)

        val gLat = toRadians(latitude1)
        val gLon = toRadians(longitude1)
        val gAlt = gLat * 0.0 + r

        val coord = doubleArrayOf(gLat, gLon, gAlt)

        // convert to rectangular coordinates
        // X-axis: defined by the vector going from Earth's center towards the intersection of the equator and Greenwitch's meridian.
        // Z-axis: axis of the geographic poles
        // Y-axis: defined by Y=Z^X
        val x = coord[2] * cos(coord[0]) * cos(coord[1])
        val y = coord[2] * cos(coord[0]) * sin(coord[1])
        val z = coord[2] * sin(coord[0])
        val xyz = doubleArrayOf(x, y, z)

        // Compute 1st rotation matrix : rotation around plane of the equator,
        // from the Greenwich meridian to the meridian containing the magnetic
        // dipole pole.
        val geolong2maglong = Array(3) { DoubleArray(3) }
        geolong2maglong[0][0] = cos(dLong)
        geolong2maglong[0][1] = sin(dLong)
        geolong2maglong[1][0] = -sin(dLong)
        geolong2maglong[1][1] = cos(dLong)
        geolong2maglong[2][2] = 1.0
        var out = dotProduct(geolong2maglong, xyz)

        // Second rotation : in the plane of the current meridian from geographic pole to magnetic dipole pole.
        val tomaglat = Array(3) { DoubleArray(3) }
        tomaglat[0][0] = cos(PI / 2 - dLat)
        tomaglat[0][2] = -sin(PI / 2 - dLat)
        tomaglat[2][0] = sin(PI / 2 - dLat)
        tomaglat[2][2] = cos(PI / 2 - dLat)
        tomaglat[1][1] = 1.0
        out = dotProduct(tomaglat, out)

        var mlat = atan2(out[2], sqrt(out[0].pow(2.0) + out[1].pow(2.0)))
        mlat = toDegrees(mlat)
        return mlat
    }

    private fun dotProduct(a: Array<DoubleArray>, b: DoubleArray): DoubleArray {
        val n = b.size
        val c = DoubleArray(n)
        for (i in 0 until n) {
            for (j in 0 until n) {
                c[i] += a[i][j] * b[j]
            }
        }
        return c
    }

    companion object {
        private const val MAGNETIC_NORTH_POLE_LATITUDE = 80.4
        private const val MAGNETIC_NORTH_POLE_LONGITUDE = -72.6
    }
}
