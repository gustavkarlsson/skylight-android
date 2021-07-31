package se.gustavkarlsson.skylight.android.lib.settings

import se.gustavkarlsson.skylight.android.core.entities.TriggerLevel
import se.gustavkarlsson.skylight.android.lib.places.PlaceId

class Settings internal constructor(
    val notificationTriggerLevel: TriggerLevel,
    val placeIdsWithNotification: Set<PlaceId>, // FIXME change to Place instead of ID
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Settings

        if (notificationTriggerLevel != other.notificationTriggerLevel) return false
        if (placeIdsWithNotification != other.placeIdsWithNotification) return false

        return true
    }

    override fun hashCode(): Int {
        var result = notificationTriggerLevel.hashCode()
        result = 31 * result + placeIdsWithNotification.hashCode()
        return result
    }

    override fun toString(): String {
        return "Settings(" +
            "notificationTriggerLevel=$notificationTriggerLevel, " +
            "placeIdsWithNotification=$placeIdsWithNotification" +
            ")"
    }
}
