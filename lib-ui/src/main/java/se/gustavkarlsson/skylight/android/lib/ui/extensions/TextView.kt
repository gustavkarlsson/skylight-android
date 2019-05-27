package se.gustavkarlsson.skylight.android.lib.ui.extensions

import android.os.Build
import android.text.Html
import android.text.method.LinkMovementMethod
import android.widget.TextView

fun TextView.setHtml(html: String) {
	val spannedHtml = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
		Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
	} else {
		@Suppress("DEPRECATION")
		Html.fromHtml(html)
	}
	text = spannedHtml
	movementMethod = LinkMovementMethod.getInstance()
}
