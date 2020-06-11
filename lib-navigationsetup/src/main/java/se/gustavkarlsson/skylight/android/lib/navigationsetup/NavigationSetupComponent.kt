package se.gustavkarlsson.skylight.android.lib.navigationsetup

interface NavigationSetupComponent {

    fun navigationInstaller(): NavigationInstaller

    interface Setter {
        fun setNavigationSetupComponent(component: NavigationSetupComponent) {
            instance = component
        }
    }

    companion object {
        lateinit var instance: NavigationSetupComponent
            private set
    }
}
