package se.gustavkarlsson.skylight.android.gui.screens.googleplayservices

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.common.GoogleApiAvailability
import com.jakewharton.rxbinding2.view.clicks
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.kotlin.autoDisposable
import kotlinx.android.synthetic.main.fragment_google_play_services.*
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.appComponent
import se.gustavkarlsson.skylight.android.extensions.appCompatActivity

class GooglePlayServicesFragment : Fragment(), LifecycleObserver {

	private val viewModel: GooglePlayServicesViewModel by lazy {
		appComponent.googlePlayServicesViewModel(this)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		appCompatActivity!!.supportActionBar!!.hide()
		lifecycle.addObserver(this)
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? = inflater.inflate(R.layout.fragment_google_play_services, container, false)


	@OnLifecycleEvent(Lifecycle.Event.ON_START)
	private fun bindData() {
		installButton.clicks()
			.autoDisposable(scope(Lifecycle.Event.ON_STOP))
			.subscribe {
				// TODO try to add callback and signal success
				GoogleApiAvailability.getInstance()
					.makeGooglePlayServicesAvailable(requireActivity())
			}
	}

	override fun onDestroy() {
		super.onDestroy()
		lifecycle.removeObserver(this)
	}
}
