package se.gustavkarlsson.skylight.android.feature.intro

import android.os.Bundle
import com.jakewharton.rxbinding2.view.clicks
import com.uber.autodispose.LifecycleScopeProvider
import com.uber.autodispose.kotlin.autoDisposable
import kotlinx.android.synthetic.main.fragment_intro.nextButton
import kotlinx.android.synthetic.main.fragment_intro.privacyPolicyLink
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import se.gustavkarlsson.skylight.android.lib.ui.BaseFragment
import se.gustavkarlsson.skylight.android.lib.ui.extensions.setHtml

internal class IntroFragment : BaseFragment() {

	override val layoutId: Int = R.layout.fragment_intro

	private val viewModel: IntroViewModel by viewModel {
		val targetId = arguments!!.getString(ARG_TARGET_ID)!!
		parametersOf(targetId)
	}

	override fun initView() {
		privacyPolicyLink.setHtml(viewModel.privacyPolicyHtml.resolve(requireContext()))
	}

	override fun bindData(scope: LifecycleScopeProvider<*>) {
		nextButton.clicks()
			.autoDisposable(scope)
			.subscribe {
				viewModel.registerScreenSeen()
				viewModel.navigateForward()
			}
	}

	companion object {
		fun newInstance(targetId: String): IntroFragment =
			IntroFragment().apply {
				arguments = Bundle().apply {
					putString(ARG_TARGET_ID, targetId)
				}
			}

		private const val ARG_TARGET_ID = "targetId"
	}
}
