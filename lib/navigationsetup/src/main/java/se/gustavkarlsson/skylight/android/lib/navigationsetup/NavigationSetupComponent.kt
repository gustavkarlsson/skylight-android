package se.gustavkarlsson.skylight.android.lib.navigationsetup

import dagger.Component

@Component(modules = [NavigationSetupModule::class])
interface NavigationSetupComponent {

    fun navigationInstaller(): NavigationInstaller

    companion object {
        fun create(): NavigationSetupComponent {
            return DaggerNavigationSetupComponent.builder().build()
        }
    }
}
