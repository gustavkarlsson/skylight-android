package se.gustavkarlsson.skylight.android.gui.activities

import android.Manifest
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import se.gustavkarlsson.skylight.android.R
import timber.log.Timber

abstract class AuroraRequirementsCheckingActivity : AppCompatActivity() {

    protected fun ensureRequirementsMet() {
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS) {
			showGooglePlayServicesNotAvailable()
        } else {
			ensureLocationPermission()
        }
    }

	private fun showGooglePlayServicesNotAvailable() {
		AlertDialog.Builder(this)
			.setIcon(R.drawable.warning_white_24dp)
			.setTitle(R.string.error_google_play_services_is_not_available_title)
			.setMessage(R.string.error_google_play_services_is_not_available_desc)
			.setPositiveButton(R.string.exit) { _, _ -> System.exit(1) }
			.setCancelable(false)
			.show()
	}

    private fun ensureLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, LOCATION_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
            showLocationPermissionRequest()
        } else {
            onRequirementsMet()
        }
    }

	private fun showLocationPermissionRequest() {
        ActivityCompat.requestPermissions(this, arrayOf(LOCATION_PERMISSION), REQUEST_CODE_LOCATION_PERMISSION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (REQUEST_CODE_LOCATION_PERMISSION == requestCode) {
            for (i in permissions.indices) {
                val permission = permissions[i]
                val result = grantResults[i]
                if (LOCATION_PERMISSION == permission) {
                    if (PERMISSION_GRANTED != result) {
                        handlePermissionDenied()
                    } else {
                        onRequirementsMet()
                    }
                }
            }
        }
    }

    private fun handlePermissionDenied() {
        Timber.i("Permission denied: %s", LOCATION_PERMISSION)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, LOCATION_PERMISSION)) {
            Timber.i("Showing rationale to user for another chance")
            showLocationPermissionRequiredDialog()
        } else {
			Timber.i("Showing permission denied dialog and exiting")
            showLocationPermissionDeniedDialog()
        }
    }

    private fun showLocationPermissionRequiredDialog() {
        AlertDialog.Builder(this)
                .setIcon(R.drawable.warning_white_24dp)
                .setTitle(getString(R.string.location_permission_required_title))
                .setMessage(R.string.location_permission_required_desc)
                .setPositiveButton(android.R.string.yes) { _, _ -> showLocationPermissionRequest() }
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
        private const val REQUEST_CODE_LOCATION_PERMISSION = 1973
	}
}
