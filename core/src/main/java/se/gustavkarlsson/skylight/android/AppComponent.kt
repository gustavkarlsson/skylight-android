package se.gustavkarlsson.skylight.android

import se.gustavkarlsson.skylight.android.services.Geocoder
import se.gustavkarlsson.skylight.android.services.PlacesRepository
import se.gustavkarlsson.skylight.android.services.Time
import javax.inject.Named

interface AppComponent {

    fun time(): Time

    @Named("versionCode")
    fun versionCode(): Int

    @Named("versionName")
    fun versionName(): String

    fun placesRepository(): PlacesRepository

    fun geocoder(): Geocoder

    interface Setter
}

@Suppress("unused")
fun AppComponent.Setter.setAppComponent(component: AppComponent) {
    appComponent = component
}

lateinit var appComponent: AppComponent
    private set
