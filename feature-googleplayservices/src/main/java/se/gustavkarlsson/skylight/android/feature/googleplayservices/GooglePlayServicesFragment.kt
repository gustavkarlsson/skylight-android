package se.gustavkarlsson.skylight.android.feature.googleplayservices

import androidx.lifecycle.Lifecycle
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding2.view.clicks
import kotlinx.android.synthetic.main.fragment_google_play_services.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import se.gustavkarlsson.koptional.optionalOf
import se.gustavkarlsson.skylight.android.lib.navigation.NavItem
import se.gustavkarlsson.skylight.android.lib.ui.BaseFragment
import se.gustavkarlsson.skylight.android.lib.ui.argument
import se.gustavkarlsson.skylight.android.lib.ui.doOnNext
import se.gustavkarlsson.skylight.android.lib.ui.extensions.bind
import se.gustavkarlsson.skylight.android.lib.ui.extensions.showErrorSnackbar
import timber.log.Timber

internal class GooglePlayServicesFragment : BaseFragment() {

    override val layoutId: Int = R.layout.fragment_google_play_services

    private val destination: NavItem by argument()

    private val viewModel: GooglePlayServicesViewModel by viewModel {
        parametersOf(destination)
    }

    override fun bindData() {
        // TODO Make this nicer
        installButton.clicks()
            .flatMapCompletable {
                viewModel.makeGooglePlayServicesAvailable(requireActivity())
            }
            .toSingleDefault(optionalOf<Throwable>(null))
            .onErrorReturn { optionalOf(it) }
            .toObservable()
            .bind(this) { (error) ->
                if (error == null) {
                    viewModel.navigateForward()
                } else {
                    Timber.e(error, "Failed to install Google Play Services")
                    view?.let { view ->
                        showErrorSnackbar(
                            view,
                            R.string.google_play_services_install_failed,
                            Snackbar.LENGTH_LONG
                        ).doOnNext(this, Lifecycle.Event.ON_DESTROY) { snackbar ->
                            snackbar.dismiss()
                        }
                    }
                }
            }
    }
}
