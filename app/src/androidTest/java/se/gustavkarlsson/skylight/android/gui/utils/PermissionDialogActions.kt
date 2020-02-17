package se.gustavkarlsson.skylight.android.gui.utils

import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice.getInstance
import androidx.test.uiautomator.UiSelector

fun allowPermission() =
    clickObjectMatching("(?i)(allow)")

fun denyPermission() =
    clickObjectMatching("(?i)(deny)")

fun denyPermissionForever() {
    clickObjectMatching("(?i)(never ask again)")
    clickObjectMatching("(?i)(deny)")
}

private fun clickObjectMatching(regex: String) =
    getInstance(getInstrumentation())
        .findObject(UiSelector().textMatches(regex))
        .click()
