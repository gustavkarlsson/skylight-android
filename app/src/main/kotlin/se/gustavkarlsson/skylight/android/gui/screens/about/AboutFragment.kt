package se.gustavkarlsson.skylight.android.gui.screens.about

import android.view.View
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.fragment_about.appByTextView
import kotlinx.android.synthetic.main.fragment_about.branchTextView
import kotlinx.android.synthetic.main.fragment_about.builtTextView
import kotlinx.android.synthetic.main.fragment_about.privacyPolicyLink
import kotlinx.android.synthetic.main.fragment_about.sha1TextView
import kotlinx.android.synthetic.main.fragment_about.toolbar
import kotlinx.android.synthetic.main.fragment_about.versionCodeTextView
import kotlinx.android.synthetic.main.fragment_about.versionNameTextView
import org.koin.androidx.viewmodel.ext.android.viewModel
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.extensions.setHtml
import se.gustavkarlsson.skylight.android.gui.BaseFragment

class AboutFragment : BaseFragment(R.layout.fragment_about) {

	private val viewModel: AboutViewModel by viewModel()

	override fun getToolbar(): Toolbar? = toolbar

	override fun initView() {
		appByTextView.text = viewModel.author.resolve(requireContext())
		versionNameTextView.text = viewModel.versionName.resolve(requireContext())
		versionCodeTextView.text = viewModel.versionCode.resolve(requireContext())
		builtTextView.text = viewModel.buildTime.resolve(requireContext())
		branchTextView.text = viewModel.branch.resolve(requireContext())
		sha1TextView.text = viewModel.sha1Compact.resolve(requireContext())
		privacyPolicyLink.setHtml(viewModel.privacyPolicyLink.resolve(requireContext()))

		val developVisibility = if (viewModel.isDevelopMode) View.VISIBLE else View.INVISIBLE
		versionCodeTextView.visibility = developVisibility
		builtTextView.visibility = developVisibility
		branchTextView.visibility = developVisibility
		sha1TextView.visibility = developVisibility
	}
}
