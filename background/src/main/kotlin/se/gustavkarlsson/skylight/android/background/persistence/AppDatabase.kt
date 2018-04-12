package se.gustavkarlsson.skylight.android.background.persistence

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import se.gustavkarlsson.skylight.android.background.persistence.typeconverters.ChanceConverters
import se.gustavkarlsson.skylight.android.background.persistence.typeconverters.InstantConverters

@Database(entities = [DbNotifiedChance::class], version = 1)
@TypeConverters(value = [InstantConverters::class, ChanceConverters::class])
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun lastNotifiedChanceDao(): LastNotifiedChanceDao

	companion object {
		@Synchronized
	    fun create(context: Context): AppDatabase =
			Room.databaseBuilder(context, AppDatabase::class.java, "skylight")
				.build()
	}
}
