package se.gustavkarlsson.skylight.android.feature.googleplayservices

import dagger.Reusable
import se.gustavkarlsson.skylight.android.lib.navigation.Backstack
import se.gustavkarlsson.skylight.android.lib.navigation.NavigationOverride
import se.gustavkarlsson.skylight.android.lib.navigation.Screen
import javax.inject.Inject

private val REQUIRING_SCREEN_NAMES = setOf(Screen.Type.Main)

@Reusable
internal class GooglePlayServicesNavigationOverride @Inject constructor(
    private val googlePlayServicesChecker: GmsGooglePlayServicesChecker,
) : NavigationOverride {
    override val priority = 8

    override fun override(
        oldBackstack: Backstack,
        targetBackstack: Backstack,
    ): Backstack? {
        return when {
            targetBackstack.screens.any { it.type in REQUIRING_SCREEN_NAMES } &&
                targetBackstack.screens.none { it.type == Screen.Type.GooglePlayServices } &&
                !googlePlayServicesChecker.isAvailable ->
                Backstack(GooglePlayServicesScreen(targetBackstack))
            else -> null
        }
    }
}
