package se.gustavkarlsson.skylight.android.gui.screens.about

import android.view.View
import kotlinx.android.synthetic.main.fragment_about.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.gui.BaseFragment

class AboutFragment : BaseFragment(R.layout.fragment_about, true) {

	private val viewModel: AboutViewModel by viewModel()

	override fun initView() {
		// TODO Replace with TextRef
		appByTextView.text = getString(R.string.about_app_by, getString(R.string.author))
		versionNameTextView.text = getString(R.string.about_version_name, viewModel.versionName)
		versionCodeTextView.text = getString(R.string.about_version_code, viewModel.versionCode)
		builtTextView.text = getString(R.string.about_built_on, viewModel.buildTime)
		branchTextView.text = getString(R.string.about_branch, viewModel.branch)
		sha1TextView.text = getString(R.string.about_sha1, viewModel.sha1Compact)

		val developVisibility = if (viewModel.isDevelopMode) View.VISIBLE else View.INVISIBLE
		versionCodeTextView.visibility = developVisibility
		builtTextView.visibility = developVisibility
		branchTextView.visibility = developVisibility
		sha1TextView.visibility = developVisibility
	}
}
