package se.gustavkarlsson.skylight.android.feature.intro

import com.jakewharton.rxbinding2.view.clicks
import com.uber.autodispose.LifecycleScopeProvider
import com.uber.autodispose.kotlin.autoDisposable
import kotlinx.android.synthetic.main.fragment_intro.nextButton
import kotlinx.android.synthetic.main.fragment_intro.privacyPolicyLink
import org.koin.androidx.viewmodel.ext.android.viewModel
import se.gustavkarlsson.skylight.android.feature.base.extensions.setHtml
import se.gustavkarlsson.skylight.android.feature.base.BaseFragment

class IntroFragment : BaseFragment() {

	override val layoutId: Int = R.layout.fragment_intro

	private val viewModel: IntroViewModel by viewModel()

	override fun initView() {
		privacyPolicyLink.setHtml(viewModel.privacyPolicyHtml.resolve(requireContext()))
	}

	override fun bindData(scope: LifecycleScopeProvider<*>) {
		nextButton.clicks()
			.autoDisposable(scope)
			.subscribe { viewModel.signalFirstRunCompleted() }
	}
}
