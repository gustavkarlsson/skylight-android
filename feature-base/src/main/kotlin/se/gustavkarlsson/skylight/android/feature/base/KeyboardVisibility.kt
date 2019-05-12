package se.gustavkarlsson.skylight.android.feature.base

import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun View.showKeyboard() {
	ContextCompat.getSystemService(
		context,
		InputMethodManager::class.java
	)?.toggleSoftInputFromWindow(applicationWindowToken, InputMethodManager.SHOW_IMPLICIT, 0)
}

fun Fragment.hideKeyboard() {
	ContextCompat.getSystemService(
		requireContext(),
		InputMethodManager::class.java
	)?.toggleSoftInputFromWindow(view!!.applicationWindowToken, 0, InputMethodManager.HIDE_NOT_ALWAYS)
}
