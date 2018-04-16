package se.gustavkarlsson.skylight.android.background.persistence

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import se.gustavkarlsson.skylight.android.background.persistence.entities.NotifiedChanceEntity

@Dao
internal interface LastNotifiedChanceDao {
    @Query("SELECT * FROM notified_chance LIMIT 1")
    fun get(): NotifiedChanceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(notifiedChanceEntity: NotifiedChanceEntity)
}
