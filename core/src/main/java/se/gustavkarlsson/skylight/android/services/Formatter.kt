package se.gustavkarlsson.skylight.android.services

import com.ioki.textref.TextRef

interface Formatter<T> {
	fun format(value: T): TextRef
}
