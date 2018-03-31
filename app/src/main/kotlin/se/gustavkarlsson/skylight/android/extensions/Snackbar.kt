package se.gustavkarlsson.skylight.android.extensions

import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewTreeObserver
import se.gustavkarlsson.skylight.android.R

fun indefiniteErrorSnackbar(view: View, message: CharSequence): Snackbar {
	return Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE)
		.apply {
			disableSwipeToDismiss()
			this.view.setBackgroundColor(
				ContextCompat.getColor(
					view.context,
					R.color.snackbar_error_background
				)
			)
		}
}

private fun Snackbar.disableSwipeToDismiss() {
	view.viewTreeObserver.addOnPreDrawListener(SnackbarDisableSwipe(view))
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
