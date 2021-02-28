package se.gustavkarlsson.skylight.android.lib.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import se.gustavkarlsson.skylight.android.lib.analytics.AnalyticsComponent

abstract class ComposeScreenFragment : Fragment() {

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val composeView = ComposeView(requireContext())
        composeView.setContent {
            MaterialTheme {
                ScreenContent()
            }
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
