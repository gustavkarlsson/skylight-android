package se.gustavkarlsson.skylight.android.gui

import android.annotation.SuppressLint
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.kotlin.autoDisposable
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.appComponent

class MainActivity : AppCompatActivity(), LifecycleObserver {

	private val store by lazy {
		appComponent.store
	}

	private val navController by lazy {
		findNavController(R.id.mainNavHost)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		setupActionBarWithNavController(navController)
		lifecycle.addObserver(this)
	}

	@SuppressLint("MissingSuperCall")
	override fun onSaveInstanceState(outState: Bundle) {
		//super.onSaveInstanceState(outState)
		// Resolves issue with navigation
	}

	override fun onSupportNavigateUp(): Boolean = navController.navigateUp()

	@OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
	private fun handleIntro() {
		store.states
			.filter {
				it.isFirstRun != null
					&& it.isGooglePlayServicesAvailable != null
					&& it.isLocationPermissionGranted != null
			}
			.distinctUntilChanged { a, b ->
				a.isFirstRun == b.isFirstRun
					&& a.isGooglePlayServicesAvailable == b.isGooglePlayServicesAvailable
					&& a.isLocationPermissionGranted == b.isLocationPermissionGranted
			}
			.autoDisposable(scope(Lifecycle.Event.ON_DESTROY))
			.subscribe {
				when {
					it.isGooglePlayServicesAvailable == false -> {
						navController.navigate(R.id.action_start_from_googlePlayServicesFragment)
					}
					it.isFirstRun == true -> {
						navController.navigate(R.id.action_start_from_introFragment)
					}
					it.isLocationPermissionGranted == false -> {
						navController.navigate(R.id.action_start_from_permissionFragment)
					}
					navController.currentDestination?.id != R.id.mainFragment -> {
						navController.navigate(R.id.action_start_from_mainFragment)
					}
				}
			}
	}
}
