package se.gustavkarlsson.skylight.android.feature.addplace

internal data class SaveDialogData(
	val suggestedName: String,
	val onSave: (finalName: String) -> Unit
)
