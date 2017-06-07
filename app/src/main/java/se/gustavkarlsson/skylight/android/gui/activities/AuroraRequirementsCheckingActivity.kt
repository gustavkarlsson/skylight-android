package se.gustavkarlsson.skylight.android.gui.activities

import android.Manifest
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.background.UpdateScheduler
import javax.inject.Inject

internal abstract class AuroraRequirementsCheckingActivity : AppCompatActivity() {

    @Inject
    lateinit var updateScheduler: UpdateScheduler

    protected fun ensureRequirementsMet() {
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            ensureLocationPermission()
        } else {
            showGooglePlayServicesNotAvailable()
        }
    }

    private fun ensureLocationPermission() {
        val hasPermission = ContextCompat.checkSelfPermission(this, LOCATION_PERMISSION) == PackageManager.PERMISSION_GRANTED
        if (!hasPermission) {
            showLocationPermissionRequest()
        } else {
            updateScheduler.setupBackgroundUpdates()
            onRequirementsMet()
        }
    }

    private fun showLocationPermissionRequest() {
        ActivityCompat.requestPermissions(this, arrayOf(LOCATION_PERMISSION), REQUEST_CODE_LOCATION_PERMISSION)
    }

    private fun showGooglePlayServicesNotAvailable() {
        AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.error_google_play_services_is_not_available_title)
                .setMessage(R.string.error_google_play_services_is_not_available_desc)
                .setPositiveButton(R.string.exit) { _, _ -> System.exit(1) }
                .setCancelable(false)
                .show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        Log.v(TAG, "onRequestPermissionsResult")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (REQUEST_CODE_LOCATION_PERMISSION == requestCode) {
            for (i in permissions.indices) {
                val permission = permissions[i]
                val result = grantResults[i]
                if (LOCATION_PERMISSION == permission) {
                    if (PERMISSION_GRANTED != result) {
                        handlePermissionDenied()
                    } else {
                        updateScheduler.setupBackgroundUpdates()
                        onRequirementsMet()
                    }
                }
            }
        }
    }

    private fun handlePermissionDenied() {
        Log.i(TAG, LOCATION_PERMISSION + " permission denied")
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, LOCATION_PERMISSION)) {
            Log.i(TAG, "Showing rationale to user for another chance")
            showLocationPermissionRequiredDialog()
        } else {
            Log.i(TAG, "Showing permission denied dialog and exiting")
            showLocationPermissionDeniedDialog()
        }
    }

    private fun showLocationPermissionRequiredDialog() {
        AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.location_permission_required_title))
                .setMessage(R.string.location_permission_required_desc)
                .setPositiveButton(android.R.string.yes) { _, _ -> showLocationPermissionRequest() }
                .setNegativeButton(R.string.exit) { _, _ -> System.exit(2) }
                .setCancelable(false)
                .show()
    }

    private fun showLocationPermissionDeniedDialog() {
        AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.error_location_permission_denied_title))
                .setMessage(R.string.error_location_permission_denied_desc)
                .setPositiveButton(R.string.exit) { _, _ -> System.exit(3) }
                .setCancelable(false)
                .show()
    }

    protected open fun onRequirementsMet() {}

    companion object {
        private val TAG = AuroraRequirementsCheckingActivity::class.java.simpleName

        val LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
        private val REQUEST_CODE_LOCATION_PERMISSION = 1973
    }
}
