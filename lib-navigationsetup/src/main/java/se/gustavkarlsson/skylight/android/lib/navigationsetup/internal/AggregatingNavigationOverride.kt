package se.gustavkarlsson.skylight.android.lib.navigationsetup.internal

import se.gustavkarlsson.skylight.android.lib.navigation.Backstack
import se.gustavkarlsson.skylight.android.lib.navigation.NavigationOverride
import timber.log.Timber

internal class AggregatingNavigationOverride(
    private val overrides: Iterable<NavigationOverride>
) : NavigationOverride {
    override val priority = 0

    override fun override(backstack: Backstack) =
        overrides.asSequence()
            .sortedByDescending { it.priority }
            .mapNotNull { it.override(backstack) }
            .firstOrNull()
            ?.also { Timber.i("Overrode $backstack with $it") }
}
