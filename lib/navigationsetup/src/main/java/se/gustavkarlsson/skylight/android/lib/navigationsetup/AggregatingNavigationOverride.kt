package se.gustavkarlsson.skylight.android.lib.navigationsetup

import se.gustavkarlsson.skylight.android.lib.navigation.Backstack
import se.gustavkarlsson.skylight.android.lib.navigation.NavigationOverride
import se.gustavkarlsson.skylight.android.logging.logInfo

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
                logInfo { "Overrode $oldBackstack with $it instead of $targetBackstack" }
            }
}
