package se.gustavkarlsson.skylight.android.lib.navigation

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
