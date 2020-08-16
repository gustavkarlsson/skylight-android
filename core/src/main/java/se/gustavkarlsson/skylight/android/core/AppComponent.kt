package se.gustavkarlsson.skylight.android.core

import android.content.Context
import io.reactivex.Scheduler
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Named

interface AppComponent {

    fun context(): Context

    @Named("versionCode")
    fun versionCode(): Int

    @Named("versionName")
    fun versionName(): String

    @Main
    fun mainThreadScheduler(): Scheduler

    @Io
    fun ioScheduler(): Scheduler

    @Main
    fun mainDispatcher(): CoroutineDispatcher

    @Io
    fun ioDispatcher(): CoroutineDispatcher

    @Computation
    fun computationScheduler(): Scheduler

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
