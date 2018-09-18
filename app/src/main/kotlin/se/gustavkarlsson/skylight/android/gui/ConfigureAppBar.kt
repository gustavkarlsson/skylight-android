package se.gustavkarlsson.skylight.android.gui

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import se.gustavkarlsson.skylight.android.extensions.appCompatActivity
import se.gustavkarlsson.skylight.android.extensions.doOnEvery

fun Fragment.configureAppBar(enabled: Boolean) {
	doOnEvery(this, Lifecycle.Event.ON_START) {
		if (enabled) {
			appCompatActivity!!.supportActionBar!!.show()
		} else {
			appCompatActivity!!.supportActionBar!!.hide()
		}
	}
}
