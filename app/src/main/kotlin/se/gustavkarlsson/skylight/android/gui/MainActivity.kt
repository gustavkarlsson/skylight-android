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

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		setupActionBarWithNavController(findNavController())
		lifecycle.addObserver(this)
	}

	@OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
	private fun handleIntro() {
		store.states
			.filter {
				it.isGooglePlayServicesAvailable == false || it.isLocationPermissionGranted == false
			}
			.firstOrError()
			.autoDisposable(scope(Lifecycle.Event.ON_DESTROY))
			.subscribe { _ ->
				findNavController().navigate(R.id.action_start_from_introFragment)
			}
	}

	@SuppressLint("MissingSuperCall")
	override fun onSaveInstanceState(outState: Bundle) {
		//super.onSaveInstanceState(outState)
		// Resolves issue with navigation
	}

	override fun onSupportNavigateUp(): Boolean = findNavController().navigateUp()

	private fun findNavController() = findNavController(R.id.mainNavHost)
}
