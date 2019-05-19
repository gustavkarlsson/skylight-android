package se.gustavkarlsson.skylight.android.gui.screens.googleplayservices

import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding2.view.clicks
import com.uber.autodispose.LifecycleScopeProvider
import com.uber.autodispose.kotlin.autoDisposable
import kotlinx.android.synthetic.main.fragment_google_play_services.installButton
import org.koin.androidx.viewmodel.ext.android.viewModel
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.extensions.showErrorSnackbar
import se.gustavkarlsson.skylight.android.feature.base.BaseFragment
import se.gustavkarlsson.skylight.android.feature.base.doOnNext
import timber.log.Timber

class GooglePlayServicesFragment : BaseFragment() {

	override val layoutId: Int = R.layout.fragment_google_play_services

	private val viewModel: GooglePlayServicesViewModel by viewModel()

	override fun bindData(scope: LifecycleScopeProvider<*>) {
		installButton.clicks()
			.flatMapCompletable {
				viewModel.makeGooglePlayServicesAvailable(requireActivity())
			}
			.autoDisposable(scope)
			.subscribe({
				viewModel.signalGooglePlayServicesInstalled()
			}, {
				Timber.e(it, "Failed to install Google Play Services")
				view?.let { view ->
					showErrorSnackbar(
						view,
						R.string.google_play_services_install_failed,
						Snackbar.LENGTH_LONG
					).doOnNext(this, ON_DESTROY) { snackbar ->
						snackbar.dismiss()
					}
				}
			})
	}
}
