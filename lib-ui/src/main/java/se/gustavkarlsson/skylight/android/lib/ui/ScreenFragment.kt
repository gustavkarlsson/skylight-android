package se.gustavkarlsson.skylight.android.lib.ui

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import de.halfbit.edgetoedge.EdgeToEdgeBuilder
import de.halfbit.edgetoedge.edgeToEdge
import se.gustavkarlsson.skylight.android.appComponent
import se.gustavkarlsson.skylight.android.navigation.navigator

abstract class ScreenFragment : BaseFragment() {

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        edgeToEdge(setupEdgeToEdge())
        toolbar?.let(::setupBackNavigation)
    }

    private fun setupBackNavigation(toolbar: Toolbar) {
        if (navigator.backstack.size > 1) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
            toolbar.setNavigationOnClickListener { navigator.closeScreen() }
        }
    }

    override fun onStart() {
        super.onStart()
        appComponent.analytics().logScreen(requireActivity(), this::class.java.simpleName)
    }

    protected open val toolbar: Toolbar? = null

    protected abstract fun setupEdgeToEdge(): EdgeToEdgeBuilder.() -> Unit
}
