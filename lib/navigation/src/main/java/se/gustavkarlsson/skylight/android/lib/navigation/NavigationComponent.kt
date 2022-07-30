package se.gustavkarlsson.skylight.android.lib.navigation

import com.squareup.anvil.annotations.ContributesTo
import se.gustavkarlsson.skylight.android.core.AppScopeMarker

@ContributesTo(AppScopeMarker::class)
interface NavigationComponent {

    fun navigator(): Navigator

    fun screens(): Screens

    interface Setter {
        fun setNavigationComponent(component: NavigationComponent) {
            instance = component
        }
    }

    companion object {
        lateinit var instance: NavigationComponent
            private set
    }
}
