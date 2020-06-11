package se.gustavkarlsson.skylight.android.feature.intro

import com.jakewharton.rxbinding2.view.clicks
import de.halfbit.edgetoedge.Edge
import de.halfbit.edgetoedge.EdgeToEdgeBuilder
import kotlinx.android.synthetic.main.fragment_intro.*
import se.gustavkarlsson.skylight.android.lib.navigation.navigator
import se.gustavkarlsson.skylight.android.lib.navigation.screens
import se.gustavkarlsson.skylight.android.lib.navigation.target
import se.gustavkarlsson.skylight.android.lib.scopedservice.getOrRegisterService
import se.gustavkarlsson.skylight.android.lib.ui.ScreenFragment
import se.gustavkarlsson.skylight.android.lib.ui.extensions.bind
import se.gustavkarlsson.skylight.android.lib.ui.extensions.setHtml

internal class IntroFragment : ScreenFragment() {

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
        privacyPolicyLink.setHtml(viewModel.privacyPolicyHtml.resolve(requireContext()))
    }

    override fun bindData() {
        myLocationButton.clicks().bind(this) {
            viewModel.registerScreenSeen()
            navigator.closeScreenAndGoTo(screens.main)
        }

        pickLocationButton.clicks().bind(this) {
            viewModel.registerScreenSeen()
            val target = requireNotNull(requireArguments().target)
            navigator.goTo(screens.addPlace(target))
        }
    }
}
