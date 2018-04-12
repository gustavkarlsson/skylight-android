package se.gustavkarlsson.skylight.android.background.persistence

import se.gustavkarlsson.skylight.android.entities.NotifiedChance

internal class RoomNotifiedRepository(db: AppDatabase) : NotifiedRepository {

	private val dao = db.lastNotifiedChanceDao()

	override fun get(): NotifiedChance? = dao.get()?.let {
		NotifiedChance(it.chance, it.timestamp)
	}

	override fun insert(notifiedChance: NotifiedChance) = notifiedChance.let {
		dao.insert(DbNotifiedChance(it.chance, it.timestamp))
	}
}
