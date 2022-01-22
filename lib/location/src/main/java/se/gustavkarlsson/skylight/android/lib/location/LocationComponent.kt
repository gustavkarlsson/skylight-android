package se.gustavkarlsson.skylight.android.lib.location

import com.squareup.anvil.annotations.ContributesTo
import se.gustavkarlsson.skylight.android.core.AppScopeMarker

@ContributesTo(AppScopeMarker::class)
interface LocationComponent {
    fun locationProvider(): LocationProvider
    fun locationServiceStatusProvider(): LocationServiceStatusProvider
    fun locationSettingsResolver(): LocationSettingsResolver

    interface Setter {
        fun setLocationComponent(component: LocationComponent) {
            instance = component
        }
    }

    companion object {
        lateinit var instance: LocationComponent
            private set
    }
}
