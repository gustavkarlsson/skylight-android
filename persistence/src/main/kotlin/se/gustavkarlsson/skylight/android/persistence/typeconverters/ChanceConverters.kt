package se.gustavkarlsson.skylight.android.persistence.typeconverters

import android.arch.persistence.room.TypeConverter
import se.gustavkarlsson.skylight.android.entities.Chance

internal class ChanceConverters {

    @TypeConverter
    fun doubleToChance(value: Double?): Chance? {
        return value?.let { Chance(it) }
    }

    @TypeConverter
    fun chanceToDouble(value: Chance?): Double? {
        return value?.value
    }
}
