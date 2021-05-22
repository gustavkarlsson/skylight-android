package se.gustavkarlsson.skylight.android.lib.navigation

import android.app.Activity

val Activity.navigator: Navigator
    get() {
        val host = this as? NavigatorHost
            ?: error("Activity does not implement ${NavigatorHost::class.java.name}")
        return host.navigator
    }

val Activity.screens: Screens
    get() {
        val host = this as? ScreensHost
            ?: error("Activity does not implement ${ScreensHost::class.java.name}")
        return host.screens
    }
