package se.gustavkarlsson.skylight.android.feature.intro

import com.google.android.material.appbar.MaterialToolbar
import de.halfbit.edgetoedge.Edge
import de.halfbit.edgetoedge.EdgeToEdgeBuilder
import io.noties.markwon.Markwon
import kotlinx.android.synthetic.main.fragment_privacypolicy.*
import se.gustavkarlsson.skylight.android.feature.privacypolicy.R
import se.gustavkarlsson.skylight.android.lib.ui.ScreenFragment

class PrivacyPolicyFragment : ScreenFragment() {

    override val layoutId: Int = R.layout.fragment_privacypolicy

    override val toolbar: MaterialToolbar get() = toolbarView

    override fun setupEdgeToEdge(): EdgeToEdgeBuilder.() -> Unit = {
        toolbarView.fit { Edge.Top }
        scrollView.fit { Edge.Bottom }
    }

    override fun initView() {
        val privacyPolicyMarkdown = resources.openRawResource(R.raw.privacy_policy)
            .bufferedReader()
            .readText()
        Markwon.create(requireContext()).setMarkdown(markdownView, privacyPolicyMarkdown)
    }
}
