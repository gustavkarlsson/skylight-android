package se.gustavkarlsson.skylight.android.feature.googleplayservices

import androidx.lifecycle.Lifecycle
import com.jakewharton.rxbinding2.view.clicks
import de.halfbit.edgetoedge.Edge
import de.halfbit.edgetoedge.EdgeToEdgeBuilder
import kotlinx.android.synthetic.main.fragment_google_play_services.*
import se.gustavkarlsson.koptional.optionalOf
import se.gustavkarlsson.skylight.android.navigation.navigator
import se.gustavkarlsson.skylight.android.navigation.target
import se.gustavkarlsson.skylight.android.lib.scopedservice.getOrRegisterService
import se.gustavkarlsson.skylight.android.lib.ui.ScreenFragment
import se.gustavkarlsson.skylight.android.lib.ui.doOnNext
import se.gustavkarlsson.skylight.android.lib.ui.extensions.bind
import se.gustavkarlsson.skylight.android.lib.ui.extensions.showSnackbar
import timber.log.Timber

internal class GooglePlayServicesFragment : ScreenFragment() {

    override val layoutId: Int = R.layout.fragment_google_play_services

    private val viewModel by lazy {
        getOrRegisterService("googlePlayServicesViewModel") {
            GooglePlayServicesComponent.viewModel()
        }
    }

    override fun setupEdgeToEdge(): EdgeToEdgeBuilder.() -> Unit = {
        playStoreImageView.fit { Edge.Top }
        installButton.fit { Edge.Bottom }
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
                    val target = requireNotNull(requireArguments().target)
                    navigator.setBackstack(target)
                } else {
                    Timber.e(error, "Failed to install Google Play Services")
                    view?.let { view ->
                        showSnackbar(
                            view,
                            R.string.google_play_services_install_failed
                        ) {
                            setIndefiniteDuration()
                            setErrorStyle()
                            setDismiss(R.string.dismiss)
                        }.doOnNext(this, Lifecycle.Event.ON_DESTROY) { snackbar ->
                            snackbar.dismiss()
                        }
                    }
                }
            }
    }
}
