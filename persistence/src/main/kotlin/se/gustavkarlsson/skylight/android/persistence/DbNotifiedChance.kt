package se.gustavkarlsson.skylight.android.persistence

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.entities.Chance

@Entity(tableName = "notified_chance")
internal class DbNotifiedChance(chance: Chance, timestamp: Instant) {
    @PrimaryKey()
    var id: Long = 1

    @ColumnInfo(name = "chance")
    var chance: Chance = chance

    @ColumnInfo(name = "timestamp")
    var timestamp: Instant = timestamp
}
