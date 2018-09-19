package se.gustavkarlsson.skylight.android.gui.screens.about

import kotlinx.android.synthetic.main.fragment_about.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.gui.BaseFragment

class AboutFragment : BaseFragment(R.layout.fragment_about, true) {

	private val viewModel: AboutViewModel by viewModel()

	override fun initView() {
		// TODO Replace with TextRef
		branchTextView.text = getString(R.string.about_branch, viewModel.branch)
		versionTextView.text = getString(R.string.about_version, viewModel.version)
		appByTextView.text = getString(R.string.about_app_by, getString(R.string.author))
		builtTextView.text = getString(R.string.about_built_on, viewModel.buildTime)
		sha1TextView.text = getString(R.string.about_sha1, viewModel.sha1Compact)
	}
}
