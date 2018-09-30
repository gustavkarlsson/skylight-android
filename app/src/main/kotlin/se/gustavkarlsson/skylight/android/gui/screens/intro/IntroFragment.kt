package se.gustavkarlsson.skylight.android.gui.screens.intro

import android.text.Html
import android.text.method.LinkMovementMethod
import com.jakewharton.rxbinding2.view.clicks
import com.uber.autodispose.LifecycleScopeProvider
import com.uber.autodispose.kotlin.autoDisposable
import kotlinx.android.synthetic.main.fragment_intro.*
import org.koin.android.ext.android.inject
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.gui.BaseFragment
import se.gustavkarlsson.skylight.android.krate.SignalFirstRunCompleted
import se.gustavkarlsson.skylight.android.krate.SkylightStore

class IntroFragment : BaseFragment(R.layout.fragment_intro) {

	// TODO Move to ViewModel
	private val store: SkylightStore by inject()

	override fun initView() {
		val html = requireContext().getString(R.string.html_privacy_policy_link, getString(R.string.privacy_policy))
		val spannedHtml = Html.fromHtml(html)
		privacyPolicyLink.text = spannedHtml
		privacyPolicyLink.movementMethod = LinkMovementMethod.getInstance()
	}

	override fun bindData(scope: LifecycleScopeProvider<*>) {
		nextButton.clicks()
			.autoDisposable(scope)
			.subscribe {
				store.issue(SignalFirstRunCompleted)
			}
	}
}
