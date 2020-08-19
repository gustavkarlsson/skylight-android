package se.gustavkarlsson.skylight.android.feature.about

import com.google.android.material.appbar.MaterialToolbar
import com.jakewharton.rxbinding3.view.clicks
import de.halfbit.edgetoedge.Edge
import de.halfbit.edgetoedge.EdgeToEdgeBuilder
import kotlinx.android.synthetic.main.fragment_about.*
import kotlinx.coroutines.CoroutineScope
import se.gustavkarlsson.skylight.android.lib.navigation.navigator
import se.gustavkarlsson.skylight.android.lib.navigation.screens
import se.gustavkarlsson.skylight.android.lib.scopedservice.getOrRegisterService
import se.gustavkarlsson.skylight.android.lib.ui.ScreenFragment
import se.gustavkarlsson.skylight.android.lib.ui.extensions.bind

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
    }

    override fun bindView(scope: CoroutineScope) {
        privacyPolicyLink.clicks()
            .bind(this) {
                navigator.goTo(screens.privacyPolicy)
            }
    }
}
