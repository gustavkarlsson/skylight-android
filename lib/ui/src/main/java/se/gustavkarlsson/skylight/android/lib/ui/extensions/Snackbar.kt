package se.gustavkarlsson.skylight.android.lib.ui.extensions

import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import com.ioki.textref.TextRef
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.lib.ui.R

fun showSnackbar(view: View, text: CharSequence, build: SnackbarBuilder.() -> Unit): Snackbar =
    showSnackbar(view, build) { setText(text) }

fun showSnackbar(view: View, @StringRes text: Int, build: SnackbarBuilder.() -> Unit): Snackbar =
    showSnackbar(view, build) { setText(text) }

fun showSnackbar(view: View, text: TextRef, build: SnackbarBuilder.() -> Unit): Snackbar =
    showSnackbar(view, build) { setText(text) }

private fun showSnackbar(
    view: View,
    build: SnackbarBuilder.() -> Unit,
    setText: SnackbarBuilder.() -> Unit
) = SnackbarBuilder(view)
    .also(setText)
    .also(build)
    .show()

class SnackbarBuilder internal constructor(view: View) {
    private val snackbar = Snackbar.make(view, "", Snackbar.LENGTH_LONG)
    private var canSwipeToDismiss = true

    fun setText(text: CharSequence) = snackbar.setText(text)

    fun setText(@StringRes text: Int) = snackbar.setText(text)

    fun setText(text: TextRef) = snackbar.setText(text.resolve(snackbar.context))

    fun setShortDuration() = snackbar.setDuration(Snackbar.LENGTH_SHORT)

    fun setLongDuration() = snackbar.setDuration(Snackbar.LENGTH_LONG)

    fun setIndefiniteDuration() = snackbar.setDuration(Snackbar.LENGTH_INDEFINITE)

    fun setDuration(duration: Duration) = snackbar.setDuration(duration.toMillis().toInt())

    fun setDismiss(text: CharSequence, onClick: (() -> Unit)? = null) {
        snackbar.setAction(text) { onClick?.invoke() }
    }

    fun setDismiss(@StringRes text: Int, onClick: (() -> Unit)? = null) {
        snackbar.setAction(text) { onClick?.invoke() }
    }

    fun setDismiss(text: TextRef, onClick: (() -> Unit)? = null) {
        snackbar.setAction(text.resolve(snackbar.context)) { onClick?.invoke() }
    }

    fun setErrorStyle() = snackbar.setErrorColors()

    internal fun show(): Snackbar = snackbar.also(Snackbar::show)
}

private fun Snackbar.setErrorColors() {
    val backgroundColor = context.theme.resolveColor(R.attr.colorError)
    view.setBackgroundColor(backgroundColor)
    val foregroundColor = context.theme.resolveColor(R.attr.colorOnError)
    setTextColor(foregroundColor)
    setActionTextColor(foregroundColor)
}
