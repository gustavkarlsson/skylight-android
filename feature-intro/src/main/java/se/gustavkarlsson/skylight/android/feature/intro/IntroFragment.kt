package se.gustavkarlsson.skylight.android.feature.intro

import com.jakewharton.rxbinding2.view.clicks
import com.uber.autodispose.LifecycleScopeProvider
import com.uber.autodispose.kotlin.autoDisposable
import kotlinx.android.synthetic.main.fragment_intro.myLocationButton
import kotlinx.android.synthetic.main.fragment_intro.pickLocationButton
import kotlinx.android.synthetic.main.fragment_intro.privacyPolicyLink
import org.koin.androidx.viewmodel.ext.android.viewModel
import se.gustavkarlsson.skylight.android.lib.ui.BaseFragment
import se.gustavkarlsson.skylight.android.lib.ui.extensions.setHtml

internal class IntroFragment : BaseFragment() {

	override val layoutId: Int = R.layout.fragment_intro

	private val viewModel: IntroViewModel by viewModel()

	override fun initView() {
		privacyPolicyLink.setHtml(viewModel.privacyPolicyHtml.resolve(requireContext()))
	}

	override fun bindData(scope: LifecycleScopeProvider<*>) {
		myLocationButton.clicks()
			.autoDisposable(scope)
			.subscribe {
				// FIXME request location here already? (similar approach as beiwagen-android?)
				viewModel.registerScreenSeen()
				viewModel.navigateToMain()
			}
		pickLocationButton.clicks()
			.autoDisposable(scope)
			.subscribe {
				viewModel.registerScreenSeen()
				viewModel.navigateToPickPlace()
			}
	}
}
