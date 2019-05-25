package se.gustavkarlsson.skylight.android.feature.base.extensions

import android.content.Context
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat

@ColorInt
fun @receiver:ColorInt Int.toArgb(context: Context): Int {
	return ContextCompat.getColor(context, this)
}
