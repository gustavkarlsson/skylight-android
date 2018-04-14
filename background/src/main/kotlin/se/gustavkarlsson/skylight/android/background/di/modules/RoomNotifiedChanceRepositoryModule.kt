package se.gustavkarlsson.skylight.android.background.di.modules

import se.gustavkarlsson.skylight.android.background.persistence.BackgroundDatabase
import se.gustavkarlsson.skylight.android.background.persistence.NotifiedChanceRepository
import se.gustavkarlsson.skylight.android.background.persistence.RoomNotifiedChanceRepository
import se.gustavkarlsson.skylight.android.di.modules.ContextModule

class RoomNotifiedChanceRepositoryModule(
	contextModule: ContextModule
) : NotifiedChanceRepositoryModule {

	override val notifiedChanceRepository: NotifiedChanceRepository by lazy {
		val db = BackgroundDatabase.create(contextModule.context)
		RoomNotifiedChanceRepository(db)
	}
}
