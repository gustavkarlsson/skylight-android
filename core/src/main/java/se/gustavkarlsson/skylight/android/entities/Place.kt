package se.gustavkarlsson.skylight.android.entities

import com.ioki.textref.TextRef
import se.gustavkarlsson.skylight.android.core.R

sealed class Place {
	abstract val name: TextRef

	object Current : Place() {
		override val name = TextRef(R.string.your_location)
	}

	data class Custom(
		val id: Long,
		override val name: TextRef,
		val location: Location
	) : Place()
}
