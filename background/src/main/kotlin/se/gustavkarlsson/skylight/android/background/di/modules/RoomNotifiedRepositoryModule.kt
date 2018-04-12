package se.gustavkarlsson.skylight.android.background.di.modules

import se.gustavkarlsson.skylight.android.background.persistence.AppDatabase
import se.gustavkarlsson.skylight.android.background.persistence.NotifiedRepository
import se.gustavkarlsson.skylight.android.background.persistence.RoomNotifiedRepository
import se.gustavkarlsson.skylight.android.di.modules.ContextModule

class RoomNotifiedRepositoryModule(
	contextModule: ContextModule
) : NotifiedRepositoryModule {

	override val notifiedRepository: NotifiedRepository by lazy {
		val db =
			AppDatabase.create(
				contextModule.context
			)
		RoomNotifiedRepository(
			db
		)
	}
}
