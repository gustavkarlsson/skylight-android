package se.gustavkarlsson.skylight.android.core

import android.content.Context
import com.squareup.anvil.annotations.ContributesTo
import kotlinx.coroutines.CoroutineDispatcher

@ContributesTo(AppScopeMarker::class)
interface AppComponent {

    fun context(): Context

    @VersionCode
    fun versionCode(): Int

    @VersionName
    fun versionName(): String

    @Main
    fun mainDispatcher(): CoroutineDispatcher

    @Io
    fun ioDispatcher(): CoroutineDispatcher

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
