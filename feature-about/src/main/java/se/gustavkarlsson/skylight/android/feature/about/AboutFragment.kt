package se.gustavkarlsson.skylight.android.feature.about

import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.fragment_about.detailsTextView
import kotlinx.android.synthetic.main.fragment_about.privacyPolicyLink
import kotlinx.android.synthetic.main.fragment_about.toolbarView
import org.koin.androidx.viewmodel.ext.android.viewModel
import se.gustavkarlsson.skylight.android.lib.ui.BaseFragment
import se.gustavkarlsson.skylight.android.lib.ui.extensions.setHtml

internal class AboutFragment : BaseFragment() {

	override val layoutId: Int = R.layout.fragment_about

	private val viewModel: AboutViewModel by viewModel()

	override val toolbar: Toolbar?
		get() = toolbarView

	override fun initView() {
		val context = requireContext()
		detailsTextView.text = viewModel.detailsText.resolve(context)
		privacyPolicyLink.setHtml(viewModel.privacyPolicyLink.resolve(context))
	}
}
