package se.gustavkarlsson.skylight.android.services.formatters

interface SingleValueFormatter<T> {
	fun format(value: T): CharSequence
}
