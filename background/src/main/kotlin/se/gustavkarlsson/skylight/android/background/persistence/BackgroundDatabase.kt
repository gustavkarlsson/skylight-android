package se.gustavkarlsson.skylight.android.background.persistence

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import se.gustavkarlsson.skylight.android.background.persistence.entities.NotifiedChanceEntity
import se.gustavkarlsson.skylight.android.background.persistence.typeconverters.ChanceConverters
import se.gustavkarlsson.skylight.android.background.persistence.typeconverters.InstantConverters

@Database(entities = [NotifiedChanceEntity::class], version = 1)
@TypeConverters(value = [InstantConverters::class, ChanceConverters::class])
internal abstract class BackgroundDatabase : RoomDatabase() {
    abstract fun lastNotifiedDao(): LastNotifiedChanceDao

	companion object {
		@Synchronized
	    fun create(context: Context): BackgroundDatabase =
			Room.databaseBuilder(context, BackgroundDatabase::class.java, "skylight")
				.build()
	}
}
