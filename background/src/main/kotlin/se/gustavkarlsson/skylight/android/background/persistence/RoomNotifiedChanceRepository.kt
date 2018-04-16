package se.gustavkarlsson.skylight.android.background.persistence

import se.gustavkarlsson.skylight.android.background.persistence.entities.NotifiedChanceEntity
import se.gustavkarlsson.skylight.android.entities.NotifiedChance

internal class RoomNotifiedChanceRepository(db: BackgroundDatabase) : NotifiedChanceRepository {

	private val dao = db.lastNotifiedDao()

	override fun get(): NotifiedChance? = dao.get()?.let {
		NotifiedChance(it.chance, it.timestamp)
	}

	override fun insert(notifiedChance: NotifiedChance) = notifiedChance.let {
		dao.insert(NotifiedChanceEntity(it.chance, it.timestamp))
	}
}
