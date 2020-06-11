package se.gustavkarlsson.skylight.android

import android.content.Context
import io.reactivex.Single
import java.util.Locale
import javax.inject.Named

interface AppComponent {

    fun context(): Context

    fun singleLocale(): Single<Locale>

    @Named("versionCode")
    fun versionCode(): Int

    @Named("versionName")
    fun versionName(): String

    fun moduleStarters(): Set<ModuleStarter>

    interface Setter {
        fun setAppComponent(component: AppComponent) {
            instance = component
        }
    }

    companion object {
        lateinit var instance: AppComponent
            internal set
    }
}
