package se.gustavkarlsson.skylight.android.lib.geomaglocation

import com.jakewharton.rx.replayingShare
import io.reactivex.Flowable
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.entities.Loadable
import se.gustavkarlsson.skylight.android.entities.LocationResult
import se.gustavkarlsson.skylight.android.entities.Report
import se.gustavkarlsson.skylight.android.services.GeomagLocationProvider
import se.gustavkarlsson.skylight.android.services.Time
import timber.log.Timber
import java.lang.Math.PI
import java.lang.Math.atan2
import java.lang.Math.cos
import java.lang.Math.pow
import java.lang.Math.sin
import java.lang.Math.sqrt
import java.lang.Math.toDegrees
import java.lang.Math.toRadians

internal class GeomagLocationProviderImpl(
	private val time: Time
) : GeomagLocationProvider {

	override fun get(location: Single<LocationResult>): Single<Report<GeomagLocation>> =
		location
			.map(::getSingleGeomagLocation)
			.doOnSuccess { Timber.i("Provided geomag location: %s", it) }

	override fun stream(
		locations: Flowable<Loadable<LocationResult>>
	): Flowable<Loadable<Report<GeomagLocation>>> =
		locations
			.switchMapSingle { loadableLocation ->
				when (loadableLocation) {
					Loadable.Loading -> Single.just(Loadable.Loading)
					is Loadable.Loaded -> {
						Single.fromCallable { getSingleGeomagLocation(loadableLocation.value) }
							.map { Loadable.Loaded(it) }
					}
				}
			}
			.distinctUntilChanged()
			.doOnNext { Timber.i("Streamed geomag location: %s", it) }
			.replayingShare(Loadable.Loading)

	private fun getSingleGeomagLocation(locationResult: LocationResult): Report<GeomagLocation> =
		locationResult.map(
			onSuccess = {
				val geomagneticLatitude = calculateGeomagneticLatitude(
					it.latitude,
					it.longitude,
					MAGNETIC_NORTH_POLE_LATITUDE,
					MAGNETIC_NORTH_POLE_LONGITUDE
				)
				Report.Success(GeomagLocation(geomagneticLatitude), time.now())
			},
			onMissingPermissionError = {
				Report.Error(R.string.error_no_location_permission, time.now())
			},
			onUnknownError = {
				Report.Error(R.string.error_no_location, time.now())
			}
		)

	// http://stackoverflow.com/a/7949249/940731
	private fun calculateGeomagneticLatitude(
		latitude1: Double,
		longitude1: Double,
		latitude2: Double,
		longitude2: Double
	): Double {
		var Dlong = longitude2
		var Dlat = latitude2
		val R = 1.0

		//convert first to radians
		Dlong = toRadians(Dlong)
		Dlat = toRadians(Dlat)

		val glat = toRadians(latitude1)
		val glon = toRadians(longitude1)
		val galt = glat * 0.0 + R

		val coord = doubleArrayOf(glat, glon, galt)

		//convert to rectangular coordinates
		//X-axis: defined by the vector going from Earth's center towards the intersection of the equator and Greenwitch's meridian.
		//Z-axis: axis of the geographic poles
		//Y-axis: defined by Y=Z^X
		val x = coord[2] * cos(coord[0]) * cos(coord[1])
		val y = coord[2] * cos(coord[0]) * sin(coord[1])
		val z = coord[2] * sin(coord[0])
		val xyz = doubleArrayOf(x, y, z)

		//Compute 1st rotation matrix : rotation around plane of the equator,
		//from the Greenwich meridian to the meridian containing the magnetic
		//dipole pole.
		val geolong2maglong = Array(3) { DoubleArray(3) }
		geolong2maglong[0][0] = cos(Dlong)
		geolong2maglong[0][1] = sin(Dlong)
		geolong2maglong[1][0] = -sin(Dlong)
		geolong2maglong[1][1] = cos(Dlong)
		geolong2maglong[2][2] = 1.0
		var out = dotProduct(geolong2maglong, xyz)


		//Second rotation : in the plane of the current meridian from geographic pole to magnetic dipole pole.
		val tomaglat = Array(3) { DoubleArray(3) }
		tomaglat[0][0] = cos(PI / 2 - Dlat)
		tomaglat[0][2] = -sin(PI / 2 - Dlat)
		tomaglat[2][0] = sin(PI / 2 - Dlat)
		tomaglat[2][2] = cos(PI / 2 - Dlat)
		tomaglat[1][1] = 1.0
		out = dotProduct(tomaglat, out)

		var mlat = atan2(out[2], sqrt(pow(out[0], 2.0) + pow(out[1], 2.0)))
		mlat = toDegrees(mlat)
		return mlat
	}

	private fun dotProduct(a: Array<DoubleArray>, b: DoubleArray): DoubleArray {
		val n = b.size
		val c = DoubleArray(n)
		for (i in 0..n - 1) {
			for (j in 0..n - 1) {
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
