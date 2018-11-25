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

	override fun setupToolbar(): Toolbar? = toolbar

	override fun initView() {
		// TODO Replace with TextRef
		appByTextView.text = getString(R.string.about_app_by, getString(R.string.author))
		versionNameTextView.text = getString(R.string.about_version_name, viewModel.versionName)
		versionCodeTextView.text = getString(R.string.about_version_code, viewModel.versionCode)
		builtTextView.text = getString(R.string.about_built_on, viewModel.buildTime)
		branchTextView.text = getString(R.string.about_branch, viewModel.branch)
		sha1TextView.text = getString(R.string.about_sha1, viewModel.sha1Compact)

		val html = getString(R.string.html_privacy_policy_link, getString(R.string.privacy_policy))
		privacyPolicyLink.setHtml(html)

		val developVisibility = if (viewModel.isDevelopMode) View.VISIBLE else View.INVISIBLE
		versionCodeTextView.visibility = developVisibility
		builtTextView.visibility = developVisibility
		branchTextView.visibility = developVisibility
		sha1TextView.visibility = developVisibility
	}
}
