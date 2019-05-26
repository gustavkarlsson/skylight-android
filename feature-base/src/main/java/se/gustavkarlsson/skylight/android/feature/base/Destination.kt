package se.gustavkarlsson.skylight.android.feature.base

import androidx.fragment.app.Fragment

data class Destination(
	val name: String,
	val priority: Int,
	val popOnLeave: Boolean,
	val createFragment: (id: String) -> Fragment?
)
