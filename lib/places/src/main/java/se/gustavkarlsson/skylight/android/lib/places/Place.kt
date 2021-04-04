package se.gustavkarlsson.skylight.android.lib.places

import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.lib.location.Location

sealed class Place {
    abstract val id: PlaceId

    object Current : Place() {
        override val id = PlaceId.Current
    }

    sealed class Saved : Place() {
        abstract override val id: PlaceId.Saved
        abstract val location: Location
        abstract val name: String
        abstract val lastChanged: Instant

        data class Favorite(
            override val id: PlaceId.Saved,
            override val name: String,
            override val location: Location,
            override val lastChanged: Instant,
        ) : Saved()

        data class Recent(
            override val id: PlaceId.Saved,
            override val name: String,
            override val location: Location,
            override val lastChanged: Instant,
        ) : Saved()
    }
}

sealed class PlaceId {
    abstract val value: Long

    object Current : PlaceId() {
        override val value: Long = -1
    }

    data class Saved(override val value: Long) : PlaceId() {
        init {
            require(value >= 0) { "Saved place ID:s must be non-negative: $value" }
        }
    }

    companion object {
        fun fromLong(longId: Long): PlaceId {
            return when (longId) {
                Current.value -> Current
                else -> Saved(longId)
            }
        }
    }
}
