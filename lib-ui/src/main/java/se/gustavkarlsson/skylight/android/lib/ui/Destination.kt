package se.gustavkarlsson.skylight.android.lib.ui

import androidx.fragment.app.Fragment

data class Destination(
	val priority: Int,
	val createFragment: (id: String) -> Fragment?
)
