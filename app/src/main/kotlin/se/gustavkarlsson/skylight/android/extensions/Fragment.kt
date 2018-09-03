package se.gustavkarlsson.skylight.android.extensions

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

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
