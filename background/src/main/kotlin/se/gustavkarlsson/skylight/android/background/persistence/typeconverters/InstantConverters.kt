package se.gustavkarlsson.skylight.android.background.persistence.typeconverters

import androidx.room.TypeConverter
import org.threeten.bp.Instant

internal class InstantConverters {

    @TypeConverter
    fun epochMillisToInstant(value: Long?): Instant? {
        return value?.let { Instant.ofEpochMilli(it) }
    }

    @TypeConverter
    fun instantToEpochMillis(value: Instant?): Long? {
        return value?.toEpochMilli()
    }
}
