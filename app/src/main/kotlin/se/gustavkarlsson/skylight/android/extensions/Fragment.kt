package se.gustavkarlsson.skylight.android.extensions

import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

val Fragment.appCompatActivity: AppCompatActivity?
	get() {
		val activity = this.activity
		return if (activity is AppCompatActivity) {
			return activity
		} else null
	}
