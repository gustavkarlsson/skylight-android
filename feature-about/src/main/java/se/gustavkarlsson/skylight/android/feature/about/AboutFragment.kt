package se.gustavkarlsson.skylight.android.feature.about

import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.fragment_about.*
import org.koin.android.ext.android.get
import se.gustavkarlsson.skylight.android.appComponent
import se.gustavkarlsson.skylight.android.lib.scopedservice.getOrRegisterService
import se.gustavkarlsson.skylight.android.lib.ui.BaseFragment
import se.gustavkarlsson.skylight.android.lib.ui.extensions.setHtml

class AboutFragment : BaseFragment() {

    override val layoutId: Int = R.layout.fragment_about

    private val viewModel by lazy {
        getOrRegisterService("aboutViewModel") { AboutComponent.viewModel() }
    }

    override val toolbar: Toolbar?
        get() = toolbarView

    override fun initView() {
        val context = requireContext()
        detailsTextView.text = viewModel.detailsText.resolve(context)
        privacyPolicyLink.setHtml(viewModel.privacyPolicyLink.resolve(context))
    }
}
