package se.gustavkarlsson.skylight.android

import se.gustavkarlsson.skylight.android.services.Time
import javax.inject.Named

interface AppComponent {

    val time: Time

    @get:Named("versionCode")
    val versionCode: Int

    @get:Named("versionName")
    val versionName: String

    interface Setter
}

@Suppress("unused")
fun AppComponent.Setter.setAppComponent(component: AppComponent) {
    appComponent = component
}

lateinit var appComponent: AppComponent
    private set
