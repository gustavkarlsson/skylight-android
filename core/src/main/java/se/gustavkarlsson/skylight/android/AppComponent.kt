package se.gustavkarlsson.skylight.android

import android.content.Context
import javax.inject.Named

interface AppComponent {

    fun context(): Context

    @Named("versionCode")
    fun versionCode(): Int

    @Named("versionName")
    fun versionName(): String

    interface Setter {
        fun setAppComponent(component: AppComponent) {
            instance = component
        }
    }

    companion object {
        lateinit var instance: AppComponent
            private set
    }
}
