package se.gustavkarlsson.skylight.android.feature.addplace

import se.gustavkarlsson.skylight.android.entities.Location

internal data class SaveDialogData(
	val suggestedName: String,
	val location: Location,
	val onSave: (finalName: String) -> Unit
)
