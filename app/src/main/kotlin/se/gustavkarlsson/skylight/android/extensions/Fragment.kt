package se.gustavkarlsson.skylight.android.extensions

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

val Fragment.appCompatActivity: AppCompatActivity?
	get() {
		val activity = this.activity
		return if (activity is AppCompatActivity) {
			return activity
		} else null
	}

inline fun <reified T : ViewModel> Fragment.getViewModel(): T {
	return ViewModelProviders.of(this).get(T::class.java)
}

inline fun <reified T : ViewModel> Fragment.getViewModel(factory: ViewModelProvider.Factory): T {
	return ViewModelProviders.of(this, factory).get(T::class.java)
}
