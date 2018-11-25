package se.gustavkarlsson.skylight.android.gui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.jakewharton.rxbinding2.support.v7.widget.itemClicks
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.kotlin.autoDisposable
import kotlinx.android.synthetic.main.activity_main.toolbar
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
		val appBarConfiguration = AppBarConfiguration(
			setOf(
				// TODO Derive from Screen
				R.id.mainFragment,
				R.id.introFragment,
				R.id.googlePlayServicesFragment,
				R.id.permissionFragment
			)
		)
		toolbar.setupWithNavController(navController, appBarConfiguration)
		addToKoin(navController)
		lifecycle.addObserver(this)
	}

	// FIXME Move to correct fragment
	@OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
	private fun bindMenu() {
		val scope = scope(Lifecycle.Event.ON_DESTROY)
		toolbar.itemClicks()
			.autoDisposable(scope)
			.subscribe { item ->
				when (item.itemId) {
					R.id.action_settings -> navigator.navigate(Screen.SETTINGS)
					R.id.action_about -> navigator.navigate(Screen.ABOUT)
				}
			}
	}

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
