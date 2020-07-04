package se.gustavkarlsson.skylight.android.lib.ui

import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun View.showKeyboard() {
    ContextCompat.getSystemService(
        context,
        InputMethodManager::class.java
    )?.showSoftInput(this, 0)
}

fun Fragment.hideKeyboard() {
    ContextCompat.getSystemService(
        requireContext(),
        InputMethodManager::class.java
    )?.hideSoftInputFromWindow(view!!.applicationWindowToken, 0)
}
