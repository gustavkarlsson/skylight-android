package se.gustavkarlsson.skylight.android.lib.navigationsetup

import se.gustavkarlsson.skylight.android.navigation.Backstack
import se.gustavkarlsson.skylight.android.navigation.NavigationOverride
import timber.log.Timber

internal class AggregatingNavigationOverride(
    private val overrides: Iterable<NavigationOverride>
) : NavigationOverride {
    override val priority = 0

    override fun override(oldBackstack: Backstack, targetBackstack: Backstack) =
        overrides.asSequence()
            .sortedByDescending { it.priority }
            .mapNotNull { it.override(oldBackstack, targetBackstack) }
            .firstOrNull()
            ?.also {
                Timber.i("Overrode $oldBackstack with $it instead of $targetBackstack")
            }
}
