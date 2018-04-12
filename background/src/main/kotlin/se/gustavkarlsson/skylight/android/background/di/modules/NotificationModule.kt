package se.gustavkarlsson.skylight.android.background.di.modules

import se.gustavkarlsson.skylight.android.background.notifications.AuroraReportNotificationDecider
import se.gustavkarlsson.skylight.android.background.notifications.Notifier
import se.gustavkarlsson.skylight.android.entities.AuroraReport

interface NotificationModule {
	val notifier: Notifier<AuroraReport>
	val decider: AuroraReportNotificationDecider
}
