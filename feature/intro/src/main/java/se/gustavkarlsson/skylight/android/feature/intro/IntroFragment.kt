package se.gustavkarlsson.skylight.android.feature.intro

import de.halfbit.edgetoedge.Edge
import de.halfbit.edgetoedge.EdgeToEdgeBuilder
import kotlinx.android.synthetic.main.fragment_intro.*
import kotlinx.coroutines.CoroutineScope
import reactivecircus.flowbinding.android.view.clicks
import se.gustavkarlsson.skylight.android.lib.navigation.navigator
import se.gustavkarlsson.skylight.android.lib.navigation.screens
import se.gustavkarlsson.skylight.android.lib.navigation.target
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionsComponent
import se.gustavkarlsson.skylight.android.lib.scopedservice.getOrRegisterService
import se.gustavkarlsson.skylight.android.lib.ui.LegacyScreenFragment
import se.gustavkarlsson.skylight.android.lib.ui.extensions.bind

internal class IntroFragment : LegacyScreenFragment() {

    override val layoutId: Int = R.layout.fragment_intro

    private val viewModel by lazy {
        getOrRegisterService("introViewModel") {
            IntroComponent.build().viewModel()
        }
    }

    override fun setupEdgeToEdge(): EdgeToEdgeBuilder.() -> Unit = {
        title.fit { Edge.Top }
        myLocationButton.fit { Edge.Bottom }
    }

    override fun initView() {
    }

    override fun bindView(scope: CoroutineScope) {
        privacyPolicyLink.clicks().bind(scope) {
            navigator.goTo(screens.privacyPolicy)
        }

        myLocationButton.clicks().bind(scope) {
            PermissionsComponent.instance.locationPermissionRequester()
                .request(this)
            viewModel.registerScreenSeen()
            navigator.closeScreenAndGoTo(screens.main)
        }

        pickLocationButton.clicks().bind(scope) {
            viewModel.registerScreenSeen()
            val target = requireNotNull(requireArguments().target)
            navigator.goTo(screens.addPlace(target))
        }
    }
}
