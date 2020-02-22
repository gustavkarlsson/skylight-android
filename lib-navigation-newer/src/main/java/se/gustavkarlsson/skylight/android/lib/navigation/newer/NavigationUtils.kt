package se.gustavkarlsson.skylight.android.lib.navigation.newer

import android.os.Bundle
import androidx.fragment.app.Fragment

val Fragment.navigator: Navigator
    get() {
        val host = requireActivity() as? NavigatorHost
            ?: error("Activity does not implement ${NavigatorHost::class.java.name}")
        return host.navigator
    }

val Fragment.screens: Screens
    get() {
        val host = requireActivity() as? ScreensHost
            ?: error("Activity does not implement ${ScreensHost::class.java.name}")
        return host.screens
    }

var Bundle.target: Backstack?
    get() = getParcelableArrayList(ARG_KEY_NAVIGATION_TARGET)
    set(value) {
        val arrayList = value?.let { ArrayList(it) }
        putParcelableArrayList(ARG_KEY_NAVIGATION_TARGET, arrayList)
    }

fun Bundle.withTarget(backstack: Backstack): Bundle =
    apply { target = backstack }

private const val ARG_KEY_NAVIGATION_TARGET = "navigation_target"
