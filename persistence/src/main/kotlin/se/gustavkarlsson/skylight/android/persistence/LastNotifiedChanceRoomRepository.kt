package se.gustavkarlsson.skylight.android.persistence

import android.content.Context
import se.gustavkarlsson.skylight.android.entities.NotifiedChance
import se.gustavkarlsson.skylight.android.services.LastNotifiedChanceRepository

class LastNotifiedChanceRoomRepository
	internal constructor(db: AppDatabase) : LastNotifiedChanceRepository {

	constructor(context: Context) : this(AppDatabase.create(context))

	private val dao = db.lastNotifiedChanceDao()

	override fun get(): NotifiedChance? = dao.get()?.let {
		NotifiedChance(it.chance, it.timestamp)
	}

	override fun insert(notifiedChance: NotifiedChance) = notifiedChance.let {
		dao.insert(DbNotifiedChance(it.chance, it.timestamp))
	}
}
