package se.gustavkarlsson.skylight.android.gui

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import se.gustavkarlsson.skylight.android.R


class MainActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		setupActionBarWithNavController(findNavController())
		ensureRequirementsMet()
	}

	override fun onSupportNavigateUp(): Boolean =
		findNavController().navigateUp()

	private fun findNavController() = findNavController(R.id.mainNavHost)

	private fun ensureRequirementsMet() {
		if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS) {
			// TODO Flesh out to handle more cases (like installs, upgrades, etc)
			showGooglePlayServicesNotAvailableDialog()
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
}
