package se.gustavkarlsson.skylight.android.feature.base

import androidx.fragment.app.Fragment

data class Destination(
	val name: String,
	val priority: Int,
	val addToBackStack: Boolean,
	val createFragment: (id: String) -> Fragment?
)
