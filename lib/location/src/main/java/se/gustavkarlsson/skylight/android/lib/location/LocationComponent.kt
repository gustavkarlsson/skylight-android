package se.gustavkarlsson.skylight.android.lib.location

interface LocationComponent {
    fun locationProvider(): LocationProvider
    fun locationServiceStatusProvider(): LocationServiceStatusProvider

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
