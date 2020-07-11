package se.gustavkarlsson.skylight.android

import android.app.Application

@Suppress("unused")
internal class Skylight : Application() {

    override fun onCreate() {
        super.onCreate()
        initStrictMode()
        initDarkMode()
        initThreeThen()
        initLogging()
        initRxJavaErrorHandling()
        initDagger()
    }
}
