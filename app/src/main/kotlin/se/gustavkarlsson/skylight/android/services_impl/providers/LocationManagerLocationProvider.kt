package se.gustavkarlsson.skylight.android.services_impl.providers

import android.annotation.SuppressLint
import android.location.Criteria
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import com.hadisatrio.optional.Optional
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.services.providers.LocationProvider
import timber.log.Timber
import java.util.concurrent.TimeUnit

class LocationManagerLocationProvider(
	private val locationManager: LocationManager,
	private val timeout: Duration
) : LocationProvider {

	@SuppressLint("MissingPermission")
	override fun get(): Single<Optional<Location>> {
		return getLocation()
			.map { Optional.of(it) }
			.subscribeOn(Schedulers.io())
			.timeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
			.doOnError { Timber.w(it, "Failed to get location") }
			.onErrorReturnItem(Optional.absent())
			.doOnSuccess { Timber.i("Provided location: %s", it) }
	}

	private fun getLocation(): Single<Location> {
		return Single.create<Location> { emitter ->
			try {
				requestUpdate(locationManager, emitter::onSuccess)
			} catch (e: Exception) {
				emitter.onError(e)
			}
		}
	}

	@SuppressLint("MissingPermission")
	private fun requestUpdate(
		locationManager: LocationManager,
		action: (Location) -> Unit
	) {
		val selectedAccuracy = when {
			locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> {
				Criteria.ACCURACY_FINE
			}
			locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> {
				Criteria.ACCURACY_COARSE
			}
			else -> throw IllegalStateException("No provided enabled")
		}
		val criteria = Criteria().apply { accuracy = selectedAccuracy }
		locationManager.requestSingleUpdate(
			criteria,
			SimpleLocationListener(action),
			Looper.getMainLooper()
		)
	}

	private class SimpleLocationListener(
		private val action: (Location) -> Unit
	) : LocationListener {
		override fun onLocationChanged(location: android.location.Location) {
			action(Location(location.latitude, location.longitude))
		}

		override fun onStatusChanged(provider: String, status: Int, extras: Bundle) = Unit
		override fun onProviderEnabled(provider: String) = Unit
		override fun onProviderDisabled(provider: String) = Unit
	}
}
