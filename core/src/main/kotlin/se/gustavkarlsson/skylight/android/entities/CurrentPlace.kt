package se.gustavkarlsson.skylight.android.entities

import com.ioki.textref.TextRef
import se.gustavkarlsson.skylight.android.core.R

data class CurrentPlace(
	override val name: TextRef = TextRef(R.string.main_your_location),
	override val auroraReport: AuroraReport? = null,
	override val notificationsEnabled: Boolean? = null,
	override val triggerLevel: ChanceLevel? = null
) : Place {
	override val id = 0
}
