package se.gustavkarlsson.skylight.android.extensions

import android.support.annotation.StringRes
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewTreeObserver
import se.gustavkarlsson.skylight.android.R

fun showErrorSnackbar(view: View, message: CharSequence, length: Int): Snackbar {
	return Snackbar.make(view, message, length)
		.setErrorColor()
		.disableSwipeToDismiss()
}

fun showErrorSnackbar(view: View, @StringRes message: Int, length: Int): Snackbar {
	return Snackbar.make(view, message, length)
		.setErrorColor()
		.disableSwipeToDismiss()
}

private fun Snackbar.setErrorColor(): Snackbar {
	view.setBackgroundColor(
		ContextCompat.getColor(
			view.context,
			R.color.snackbar_error_background
		)
	)
	return this
}

private fun Snackbar.disableSwipeToDismiss(): Snackbar {
	view.viewTreeObserver.addOnPreDrawListener(SnackbarDisableSwipe(view))
	return this
}

private class SnackbarDisableSwipe(private val snackbarView: View) :
	ViewTreeObserver.OnPreDrawListener {
	override fun onPreDraw(): Boolean {
		snackbarView.viewTreeObserver.removeOnPreDrawListener(this)
		val layoutParams = snackbarView.layoutParams
		if (layoutParams is CoordinatorLayout.LayoutParams) {
			layoutParams.behavior = null
		}
		return true
	}
}
