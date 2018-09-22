package se.gustavkarlsson.skylight.android.gui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.kotlin.autoDisposable
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.ext.android.bindScope
import org.koin.androidx.scope.ext.android.createScope
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.extensions.addToKoin
import se.gustavkarlsson.skylight.android.krate.SkylightStore
import se.gustavkarlsson.skylight.android.navigation.Navigator
import se.gustavkarlsson.skylight.android.navigation.Screen

class MainActivity : AppCompatActivity(), LifecycleObserver {

	private val store: SkylightStore by inject()

	private val navigator: Navigator by inject()

	private val navController by lazy {
		findNavController(R.id.mainNavHost)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		bindScope(createScope("activity"))
		setContentView(R.layout.activity_main)
		addToKoin(navController)
		setupActionBarWithNavController(navController)
		lifecycle.addObserver(this)
	}

	override fun onSupportNavigateUp(): Boolean = navController.navigateUp()

	@OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
	private fun handleInitialNavigation() {
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
			.map {
				when {
					it.isGooglePlayServicesAvailable == false -> {
						Screen.GOOGLE_PLAY_SERVICES
					}
					it.isFirstRun == true -> {
						Screen.INTRO
					}
					it.isLocationPermissionGranted == false -> {
						Screen.PERMISSION
					}
					else -> {
						Screen.MAIN
					}
				}
			}
			.autoDisposable(scope(Lifecycle.Event.ON_DESTROY))
			.subscribe(navigator::navigate)
	}
}
