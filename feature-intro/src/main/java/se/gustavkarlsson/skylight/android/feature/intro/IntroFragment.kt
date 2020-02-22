package se.gustavkarlsson.skylight.android.feature.intro

import com.jakewharton.rxbinding2.view.clicks
import kotlinx.android.synthetic.main.fragment_intro.myLocationButton
import kotlinx.android.synthetic.main.fragment_intro.pickLocationButton
import kotlinx.android.synthetic.main.fragment_intro.privacyPolicyLink
import org.koin.androidx.viewmodel.ext.android.viewModel
import se.gustavkarlsson.skylight.android.lib.navigation.navigator
import se.gustavkarlsson.skylight.android.lib.navigation.screens
import se.gustavkarlsson.skylight.android.lib.navigation.target
import se.gustavkarlsson.skylight.android.lib.ui.BaseFragment
import se.gustavkarlsson.skylight.android.lib.ui.extensions.bind
import se.gustavkarlsson.skylight.android.lib.ui.extensions.setHtml

internal class IntroFragment : BaseFragment() {

    override val layoutId: Int = R.layout.fragment_intro

    private val viewModel: IntroViewModel by viewModel()

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
