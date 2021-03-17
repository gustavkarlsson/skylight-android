package se.gustavkarlsson.skylight.android.lib.ui.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import se.gustavkarlsson.skylight.android.lib.analytics.AnalyticsComponent
import se.gustavkarlsson.skylight.android.lib.ui.ScopeFragment

abstract class ComposeScreenFragment : ScopeFragment() {

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val composeView = ComposeView(requireContext())
        composeView.setContent {
            ScreenContent()
        }
        return composeView
    }

    @Composable
    protected abstract fun ScreenContent()

    @CallSuper
    override fun onStart() {
        super.onStart()
        AnalyticsComponent.instance.analytics().logScreen(requireActivity(), this::class.java.simpleName)
    }
}
