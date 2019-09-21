package se.gustavkarlsson.skylight.android.lib.places

import com.ioki.textref.TextRef
import se.gustavkarlsson.skylight.android.entities.Location

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
