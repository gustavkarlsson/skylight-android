package se.gustavkarlsson.skylight.android.lib.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import se.gustavkarlsson.skylight.android.lib.analytics.AnalyticsComponent

abstract class ComposeScreenFragment : Fragment() {

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply { render() }

    protected abstract fun ComposeView.render()

    @CallSuper
    override fun onStart() {
        super.onStart()
        AnalyticsComponent.instance.analytics().logScreen(requireActivity(), this::class.java.simpleName)
    }
}
