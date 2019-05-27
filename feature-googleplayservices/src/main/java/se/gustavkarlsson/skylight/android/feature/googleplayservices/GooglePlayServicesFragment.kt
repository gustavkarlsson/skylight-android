package se.gustavkarlsson.skylight.android.feature.googleplayservices

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding2.view.clicks
import com.uber.autodispose.LifecycleScopeProvider
import com.uber.autodispose.kotlin.autoDisposable
import kotlinx.android.synthetic.main.fragment_google_play_services.installButton
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import se.gustavkarlsson.skylight.android.lib.ui.BaseFragment
import se.gustavkarlsson.skylight.android.lib.ui.doOnNext
import se.gustavkarlsson.skylight.android.lib.ui.extensions.showErrorSnackbar
import timber.log.Timber

internal class GooglePlayServicesFragment : BaseFragment() {

	override val layoutId: Int = R.layout.fragment_google_play_services

	private val viewModel: GooglePlayServicesViewModel by viewModel {
		val targetId = arguments!!.getString(ARG_TARGET_ID)!!
		parametersOf(targetId)
	}

	override fun bindData(scope: LifecycleScopeProvider<*>) {
		installButton.clicks()
			.flatMapCompletable {
				viewModel.makeGooglePlayServicesAvailable(requireActivity())
			}
			.autoDisposable(scope)
			.subscribe({
				viewModel.navigateForward()
			}, {
				Timber.e(it, "Failed to install Google Play Services")
				view?.let { view ->
					showErrorSnackbar(
						view,
						R.string.google_play_services_install_failed,
						Snackbar.LENGTH_LONG
					).doOnNext(this, Lifecycle.Event.ON_DESTROY) { snackbar ->
						snackbar.dismiss()
					}
				}
			})
	}

	companion object {
		fun newInstance(targetId: String): GooglePlayServicesFragment =
			GooglePlayServicesFragment().apply {
				arguments = Bundle().apply {
					putString(ARG_TARGET_ID, targetId)
				}
			}

		private const val ARG_TARGET_ID = "targetId"
	}
}
