package se.gustavkarlsson.skylight.android.feature.addplace

internal data class SearchResultItem(
	val text: CharSequence,
	val onClick: () -> Unit
)
