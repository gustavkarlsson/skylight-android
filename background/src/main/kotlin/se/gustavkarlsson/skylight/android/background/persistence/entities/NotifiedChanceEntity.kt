package se.gustavkarlsson.skylight.android.background.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.entities.Chance

@Entity(tableName = "notified_chance")
internal class NotifiedChanceEntity(chance: Chance, timestamp: Instant) {
    @PrimaryKey()
    var id: Long = 1

    @ColumnInfo(name = "chance")
    var chance: Chance = chance

    @ColumnInfo(name = "timestamp")
    var timestamp: Instant = timestamp
}
