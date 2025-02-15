package se.gustavkarlsson.skylight.android

import android.app.Application
import kotlinx.coroutines.runBlocking
import se.gustavkarlsson.skylight.android.initializers.initDarkMode
import se.gustavkarlsson.skylight.android.initializers.initDependencies
import se.gustavkarlsson.skylight.android.initializers.initLogging
import se.gustavkarlsson.skylight.android.initializers.initStrictMode
import se.gustavkarlsson.skylight.android.initializers.runMigrations
import se.gustavkarlsson.skylight.android.initializers.startModules

@Suppress("unused")
internal class Skylight : Application() {

    override fun onCreate() {
        super.onCreate()
        initLogging()
        runMigrations()
        initDependencies()
        initDarkMode()
        initStrictMode()
        runBlocking {
            startModules()
        }
    }
}
