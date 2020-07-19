package se.gustavkarlsson.skylight.android.lib.ui.extensions

import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.ioki.textref.TextRef
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.lib.ui.R

suspend fun showSnackbar(view: View, text: CharSequence, build: SnackbarBuilder.() -> Unit): Snackbar =
    showSnackbar { buildSnackbar(view, build) { setText(text) } }

suspend fun showSnackbar(view: View, @StringRes text: Int, build: SnackbarBuilder.() -> Unit): Snackbar =
    showSnackbar { buildSnackbar(view, build) { setText(text) } }

suspend fun showSnackbar(view: View, text: TextRef, build: SnackbarBuilder.() -> Unit): Snackbar =
    showSnackbar { buildSnackbar(view, build) { setText(text) } }

suspend fun showSnackbar(
    buildSnackbar: () -> Snackbar
): Snackbar =
    withContext(Dispatchers.IO + CoroutineName("showSnackbar")) {
        suspendCancellableCoroutine<Snackbar> { cont ->
            try {
                val snackbar = buildSnackbar()
                snackbar.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    override fun onDismissed(snackbar: Snackbar, event: Int) {
                        cont.cancel()
                    }
                })
                cont.invokeOnCancellation {
                    snackbar.dismiss()
                }
                snackbar.show()
                cont.resume(snackbar)
            } catch (e: Exception) {
                cont.resumeWithException(e)
            }
        }
    }

private fun buildSnackbar(
    view: View,
    setText: SnackbarBuilder.() -> Unit,
    build: SnackbarBuilder.() -> Unit
) = SnackbarBuilder(view)
    .also(setText)
    .also(build)
    .build()

class SnackbarBuilder internal constructor(view: View) {
    private val snackbar = Snackbar.make(view, "", Snackbar.LENGTH_LONG)

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

    internal fun build(): Snackbar = snackbar
}

private fun Snackbar.setErrorColors() {
    val backgroundColor = context.theme.resolveColor(R.attr.colorError)
    view.setBackgroundColor(backgroundColor)
    val foregroundColor = context.theme.resolveColor(R.attr.colorOnError)
    setTextColor(foregroundColor)
    setActionTextColor(foregroundColor)
}
