package se.gustavkarlsson.skylight.android.gui

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.appComponent


class MainActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		setupActionBarWithNavController(findNavController())
		if (displayIntro()) {
			findNavController().navigate(R.id.action_start_from_introFragment)
		}
	}

	@SuppressLint("MissingSuperCall")
	override fun onSaveInstanceState(outState: Bundle) {
		//super.onSaveInstanceState(outState)
		// Resolves issue with navigation
	}

	private fun displayIntro(): Boolean {
		return (!(googleApiIsAvailable()) || !(locationPermissionGranted()))
	}

	private fun googleApiIsAvailable() = GoogleApiAvailability.getInstance()
			.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS

	private fun locationPermissionGranted() = ContextCompat.checkSelfPermission(
		this,
		appComponent.locationPermission
	) == PackageManager.PERMISSION_GRANTED

	override fun onSupportNavigateUp(): Boolean =
		findNavController().navigateUp()

	private fun findNavController() = findNavController(R.id.mainNavHost)
}
