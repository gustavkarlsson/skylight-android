package se.gustavkarlsson.skylight.android.services_impl

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.services.GooglePlayServicesChecker
import timber.log.Timber

class GmsGooglePlayServicesChecker(private val context: Context) : GooglePlayServicesChecker {

	private val isAvailableRelay = BehaviorRelay.createDefault(isAvailable())

	private fun isAvailable(): Boolean {
		val result = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
		return result == ConnectionResult.SUCCESS
	}

	override val isAvailable: Flowable<Boolean> = isAvailableRelay.toFlowable(BackpressureStrategy.LATEST)

	override fun signalInstalled() {
		Timber.d("Signalled Google Play Services installed")
		isAvailableRelay.accept(true)
	}

}
