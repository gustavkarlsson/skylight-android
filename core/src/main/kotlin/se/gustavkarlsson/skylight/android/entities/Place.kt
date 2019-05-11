package se.gustavkarlsson.skylight.android.entities

import com.ioki.textref.TextRef

interface Place {
	val id: Int
	val name: TextRef
	val auroraReport: AuroraReport?
	val notificationsEnabled: Boolean?
	val triggerLevel: ChanceLevel?
}
