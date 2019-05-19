package se.gustavkarlsson.skylight.android.formatters

import com.ioki.textref.TextRef

interface SingleValueFormatter<T> {
	fun format(value: T): TextRef
}
