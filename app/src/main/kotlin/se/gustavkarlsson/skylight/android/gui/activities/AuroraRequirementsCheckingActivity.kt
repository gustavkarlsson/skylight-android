package se.gustavkarlsson.skylight.android.gui.activities

import android.Manifest
import android.arch.lifecycle.Lifecycle
import android.support.v7.app.AlertDialog
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.tbruyelle.rxpermissions2.RxPermissions
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.kotlin.autoDisposable
import se.gustavkarlsson.skylight.android.BuildConfig
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.gui.LifecycleSchedulingActivity
import timber.log.Timber

abstract class AuroraRequirementsCheckingActivity : LifecycleSchedulingActivity() {

	private val rxPermissions: RxPermissions by lazy {
		RxPermissions(this)
			.apply { setLogging(BuildConfig.DEBUG) }
	}

    protected fun ensureRequirementsMet() {
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS) {
			// TODO Flesh out to handle more cases (like installs, upgrades, etc)
			showGooglePlayServicesNotAvailableDialog()
        } else {
			ensureLocationPermission()
        }
    }

	private fun showGooglePlayServicesNotAvailableDialog() {
		AlertDialog.Builder(this)
			.setIcon(R.drawable.warning_white_24dp)
			.setTitle(R.string.error_google_play_services_is_not_available_title)
			.setMessage(R.string.error_google_play_services_is_not_available_desc)
			.setPositiveButton(R.string.exit) { _, _ -> System.exit(1) }
			.setCancelable(false)
			.show()
	}

    private fun ensureLocationPermission() {
		rxPermissions
			.requestEach(LOCATION_PERMISSION)
			.autoDisposable(scope(Lifecycle.Event.ON_DESTROY))
			.subscribe {
				when {
					it.granted -> {
						Timber.i("Permission met")
						onRequirementsMet()
					}
					it.shouldShowRequestPermissionRationale -> {
						Timber.i("Showing permission rationale to user for another chance")
						showLocationPermissionRequiredDialog()
					}
					else -> {
						Timber.i("Showing permission denied dialog and exiting")
						showLocationPermissionDeniedDialog()
					}
				}
			}
    }

    private fun showLocationPermissionRequiredDialog() {
        AlertDialog.Builder(this)
                .setIcon(R.drawable.warning_white_24dp)
                .setTitle(getString(R.string.location_permission_required_title))
                .setMessage(R.string.location_permission_required_desc)
                .setPositiveButton(android.R.string.yes) { _, _ -> ensureLocationPermission() }
                .setNegativeButton(R.string.exit) { _, _ -> System.exit(2) }
                .setCancelable(false)
                .show()
    }

    private fun showLocationPermissionDeniedDialog() {
        AlertDialog.Builder(this)
                .setIcon(R.drawable.warning_white_24dp)
                .setTitle(getString(R.string.error_location_permission_denied_title))
                .setMessage(R.string.error_location_permission_denied_desc)
                .setPositiveButton(R.string.exit) { _, _ -> System.exit(3) }
                .setCancelable(false)
                .show()
    }

    abstract fun onRequirementsMet()

	companion object {
        private const val LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
	}
}
