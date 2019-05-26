package se.gustavkarlsson.skylight.android.feature.about

import android.view.View
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.fragment_about.appByTextView
import kotlinx.android.synthetic.main.fragment_about.branchTextView
import kotlinx.android.synthetic.main.fragment_about.builtTextView
import kotlinx.android.synthetic.main.fragment_about.privacyPolicyLink
import kotlinx.android.synthetic.main.fragment_about.sha1TextView
import kotlinx.android.synthetic.main.fragment_about.toolbarView
import kotlinx.android.synthetic.main.fragment_about.versionCodeTextView
import kotlinx.android.synthetic.main.fragment_about.versionNameTextView
import org.koin.androidx.viewmodel.ext.android.viewModel
import se.gustavkarlsson.skylight.android.feature.base.extensions.setHtml
import se.gustavkarlsson.skylight.android.feature.base.BaseFragment

internal class AboutFragment : BaseFragment() {

	override val layoutId: Int = R.layout.fragment_about

	private val viewModel: AboutViewModel by viewModel()

	override val toolbar: Toolbar?
		get() = toolbarView

	override fun initView() {
		val context = requireContext()
		appByTextView.text = viewModel.author.resolve(context)
		versionNameTextView.text = viewModel.versionName.resolve(context)
		versionCodeTextView.text = viewModel.versionCode.resolve(context)
		builtTextView.text = viewModel.buildTime.resolve(context)
		branchTextView.text = viewModel.branch.resolve(context)
		sha1TextView.text = viewModel.sha1Compact.resolve(context)
		privacyPolicyLink.setHtml(viewModel.privacyPolicyLink.resolve(context))

		val developVisibility = if (viewModel.isDevelopMode) View.VISIBLE else View.INVISIBLE
		versionCodeTextView.visibility = developVisibility
		builtTextView.visibility = developVisibility
		branchTextView.visibility = developVisibility
		sha1TextView.visibility = developVisibility
	}
}
