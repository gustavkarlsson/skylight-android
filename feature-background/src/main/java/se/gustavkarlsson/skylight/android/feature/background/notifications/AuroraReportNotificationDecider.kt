package se.gustavkarlsson.skylight.android.feature.background.notifications

import se.gustavkarlsson.skylight.android.entities.AuroraReport

internal interface AuroraReportNotificationDecider {
	fun shouldNotify(newReport: AuroraReport): Boolean
	fun onNotified(notifiedReport: AuroraReport)
}
