package se.gustavkarlsson.skylight.android.feature.addplace

import com.ioki.textref.TextRef

internal data class SearchResultItem(
	val text: String,
	val onClick: () -> Unit
)
