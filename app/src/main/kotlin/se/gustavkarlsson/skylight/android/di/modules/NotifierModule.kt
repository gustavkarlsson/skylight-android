package se.gustavkarlsson.skylight.android.di.modules

import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.LastNotifiedChanceRepository
import se.gustavkarlsson.skylight.android.services.Notifier

interface NotifierModule {
	val lastNotifiedChanceRepository: LastNotifiedChanceRepository
	val notifier: Notifier<AuroraReport>
}
