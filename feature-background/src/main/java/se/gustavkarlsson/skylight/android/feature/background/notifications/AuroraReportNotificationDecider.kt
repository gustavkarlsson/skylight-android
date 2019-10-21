package se.gustavkarlsson.skylight.android.feature.background.notifications

import se.gustavkarlsson.skylight.android.entities.CompleteAuroraReport

internal interface AuroraReportNotificationDecider {
	fun shouldNotify(newReport: CompleteAuroraReport): Boolean
	fun onNotified(notifiedReport: CompleteAuroraReport)
}
