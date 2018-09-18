package se.gustavkarlsson.skylight.android.gui.screens.googleplayservices

import com.google.android.gms.common.GoogleApiAvailability
import com.jakewharton.rxbinding2.view.clicks
import com.uber.autodispose.LifecycleScopeProvider
import com.uber.autodispose.kotlin.autoDisposable
import kotlinx.android.synthetic.main.fragment_google_play_services.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.gui.BaseFragment

class GooglePlayServicesFragment : BaseFragment(R.layout.fragment_google_play_services, false) {

	private val viewModel: GooglePlayServicesViewModel by viewModel()

	override fun bindData(scope: LifecycleScopeProvider<*>) {
		installButton.clicks()
			.autoDisposable(scope)
			.subscribe {
				// TODO Wait for https://github.com/ashdavies/rx-tasks/issues/21
				GoogleApiAvailability.getInstance()
					.makeGooglePlayServicesAvailable(requireActivity())
			}
	}
}
