package se.gustavkarlsson.skylight.android.feature.about

import com.google.android.material.appbar.MaterialToolbar
import de.halfbit.edgetoedge.Edge
import de.halfbit.edgetoedge.EdgeToEdgeBuilder
import kotlinx.android.synthetic.main.fragment_about.*
import se.gustavkarlsson.skylight.android.lib.scopedservice.getOrRegisterService
import se.gustavkarlsson.skylight.android.lib.ui.ScreenFragment
import se.gustavkarlsson.skylight.android.lib.ui.extensions.setHtml

class AboutFragment : ScreenFragment() {

    override val layoutId: Int = R.layout.fragment_about

    private val viewModel by lazy {
        getOrRegisterService("aboutViewModel") { AboutComponent.build().viewModel() }
    }

    override val toolbar: MaterialToolbar get() = toolbarView

    override fun setupEdgeToEdge(): EdgeToEdgeBuilder.() -> Unit = {
        toolbarView.fit { Edge.Top }
    }

    override fun initView() {
        val context = requireContext()
        detailsTextView.text = viewModel.detailsText.resolve(context)
        privacyPolicyLink.setHtml(viewModel.privacyPolicyLink.resolve(context))
    }
}
