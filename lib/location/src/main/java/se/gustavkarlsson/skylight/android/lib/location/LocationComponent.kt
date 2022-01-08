package se.gustavkarlsson.skylight.android.lib.location

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Qualifier

interface LocationComponent {
    fun locationProvider(): LocationProvider

    @LocationServiceStatus
    fun mutableLocationServiceStatus(): MutableStateFlow<Boolean>

    @LocationServiceStatus
    fun locationServiceStatus(): StateFlow<Boolean>

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

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class LocationServiceStatus
