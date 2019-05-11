package se.gustavkarlsson.skylight.android.entities

import com.ioki.textref.TextRef
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.entities.Place

data class CustomPlace(
	override val id: Int,
	override val name: TextRef,
	val location: Location,
	override val auroraReport: AuroraReport? = null,
	override val notificationsEnabled: Boolean? = null,
	override val triggerLevel: ChanceLevel? = null
) : Place
