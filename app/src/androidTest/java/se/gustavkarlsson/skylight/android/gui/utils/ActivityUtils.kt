package se.gustavkarlsson.skylight.android.gui.utils

import android.app.Activity
import assertk.assertions.isTrue

fun Activity.verifyIsFinishing() {
	assertk.assert(isFinishing, "activity is finishing").isTrue()
}
