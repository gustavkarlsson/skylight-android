package se.gustavkarlsson.skylight.android.entities

import com.ioki.textref.TextRef
import se.gustavkarlsson.skylight.android.core.R

sealed class Place {
	abstract val id: Int
	abstract val name: TextRef
	abstract val auroraReport: AuroraReport?
	abstract val notificationsEnabled: Boolean?
	abstract val triggerLevel: ChanceLevel?

	data class Current(
		override val auroraReport: AuroraReport? = null,
		override val notificationsEnabled: Boolean? = null,
		override val triggerLevel: ChanceLevel? = null
	) : Place() {
		override val id get() = ID
		override val name get() = NAME

		companion object {
			const val ID: Int = 0
			val NAME: TextRef = TextRef(R.string.main_your_location)
		}
	}

	data class Custom(
		override val id: Int,
		override val name: TextRef,
		val location: Location,
		override val auroraReport: AuroraReport? = null,
		override val notificationsEnabled: Boolean? = null,
		override val triggerLevel: ChanceLevel? = null
	) : Place()
}
