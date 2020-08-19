package se.gustavkarlsson.skylight.android.feature.googleplayservices

import de.halfbit.edgetoedge.Edge
import de.halfbit.edgetoedge.EdgeToEdgeBuilder
import kotlinx.android.synthetic.main.fragment_google_play_services.*
import kotlinx.coroutines.CoroutineScope
import reactivecircus.flowbinding.android.view.clicks
import se.gustavkarlsson.skylight.android.core.logging.logError
import se.gustavkarlsson.skylight.android.lib.navigation.navigator
import se.gustavkarlsson.skylight.android.lib.navigation.target
import se.gustavkarlsson.skylight.android.lib.scopedservice.getOrRegisterService
import se.gustavkarlsson.skylight.android.lib.ui.ScreenFragment
import se.gustavkarlsson.skylight.android.lib.ui.extensions.bind
import se.gustavkarlsson.skylight.android.lib.ui.extensions.showSnackbar

internal class GooglePlayServicesFragment : ScreenFragment() {

    override val layoutId: Int = R.layout.fragment_google_play_services

    private val viewModel by lazy {
        getOrRegisterService("googlePlayServicesViewModel") {
            GooglePlayServicesComponent.build().viewModel()
        }
    }

    override fun setupEdgeToEdge(): EdgeToEdgeBuilder.() -> Unit = {
        playStoreImageView.fit { Edge.Top }
        installButton.fit { Edge.Bottom }
    }

    override fun initView() = Unit

    override fun bindView(scope: CoroutineScope) {
        installButton.clicks().bind(scope) {
            try {
                makeAvailable()
            } catch (e: Exception) {
                showError(e)
            }
        }
    }

    private suspend fun makeAvailable() {
        viewModel.makeGooglePlayServicesAvailable(requireActivity())
        val target = requireNotNull(requireArguments().target)
        navigator.setBackstack(target)
    }

    private suspend fun showError(error: Exception) {
        logError(error) { "Failed to install Google Play Services" }
        view?.let { view ->
            showSnackbar(
                view,
                R.string.google_play_services_install_failed
            ) {
                setIndefiniteDuration()
                setErrorStyle()
                setDismiss(R.string.dismiss)
            }
        }
    }
}
