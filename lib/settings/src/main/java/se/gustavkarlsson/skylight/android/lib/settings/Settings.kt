package se.gustavkarlsson.skylight.android.lib.settings

import se.gustavkarlsson.skylight.android.core.entities.TriggerLevel
import se.gustavkarlsson.skylight.android.lib.places.PlaceId

class Settings internal constructor(
    internal val map: Map<PlaceId, TriggerLevel>,
) {
    operator fun get(placeId: PlaceId): TriggerLevel {
        return map[placeId] ?: TriggerLevel.NEVER
    }

    fun asMap(): Map<PlaceId, TriggerLevel> = map

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Settings

        if (map != other.map) return false

        return true
    }

    override fun hashCode(): Int {
        return map.hashCode()
    }

    override fun toString(): String {
        return "NotificationTriggerLevels(map=$map)"
    }
}
