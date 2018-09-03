package se.gustavkarlsson.skylight.android.background.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import se.gustavkarlsson.skylight.android.background.persistence.entities.NotifiedChanceEntity

@Dao
internal interface LastNotifiedChanceDao {
    @Query("SELECT * FROM notified_chance LIMIT 1")
    fun get(): NotifiedChanceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(notifiedChanceEntity: NotifiedChanceEntity)
}
