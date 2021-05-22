package se.gustavkarlsson.skylight.android

import android.app.Application
import se.gustavkarlsson.skylight.android.initializers.initDagger
import se.gustavkarlsson.skylight.android.initializers.initDarkMode
import se.gustavkarlsson.skylight.android.initializers.initLogging
import se.gustavkarlsson.skylight.android.initializers.initStrictMode
import se.gustavkarlsson.skylight.android.initializers.initThreeThen
import se.gustavkarlsson.skylight.android.initializers.runMigrations

@Suppress("unused")
internal class Skylight : Application() {

    override fun onCreate() {
        super.onCreate()
        initDarkMode()
        initLogging()
        initStrictMode()
        initThreeThen()
        initDagger()
        runMigrations()
    }
}
