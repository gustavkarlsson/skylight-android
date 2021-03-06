package se.gustavkarlsson.skylight.android.lib.navigation

interface NavigationComponent {

    fun navigationOverrides(): Set<NavigationOverride>

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
