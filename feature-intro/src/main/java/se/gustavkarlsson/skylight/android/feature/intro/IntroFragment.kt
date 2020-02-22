package se.gustavkarlsson.skylight.android.feature.intro

import com.jakewharton.rxbinding2.view.clicks
import kotlinx.android.synthetic.main.fragment_intro.*
import org.koin.android.ext.android.get
import se.gustavkarlsson.skylight.android.navigation.navigator
import se.gustavkarlsson.skylight.android.navigation.screens
import se.gustavkarlsson.skylight.android.navigation.target
import se.gustavkarlsson.skylight.android.lib.scopedservice.getOrRegisterService
import se.gustavkarlsson.skylight.android.lib.ui.BaseFragment
import se.gustavkarlsson.skylight.android.lib.ui.extensions.bind
import se.gustavkarlsson.skylight.android.lib.ui.extensions.setHtml

internal class IntroFragment : BaseFragment() {

    override val layoutId: Int = R.layout.fragment_intro

    private val viewModel by lazy {
        getOrRegisterService("introViewModel") {
            IntroComponent.viewModel()
        }
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
