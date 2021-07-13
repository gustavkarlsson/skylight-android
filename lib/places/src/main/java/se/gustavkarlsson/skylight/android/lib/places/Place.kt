package se.gustavkarlsson.skylight.android.lib.places

import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.lib.location.Location

sealed interface Place {
    val id: PlaceId

    object Current : Place {
        override val id = PlaceId.Current
    }

    sealed interface Saved : Place {
        abstract override val id: PlaceId.Saved
        val location: Location
        val name: String
        val lastChanged: Instant

        data class Favorite(
            override val id: PlaceId.Saved,
            override val name: String,
            override val location: Location,
            override val lastChanged: Instant,
        ) : Saved

        data class Recent(
            override val id: PlaceId.Saved,
            override val name: String,
            override val location: Location,
            override val lastChanged: Instant,
        ) : Saved
    }
}

val Place.savedLocation: Location?
    get() = when (this) {
        Place.Current -> null
        is Place.Saved -> location
    }

sealed interface PlaceId {
    val value: Long

    object Current : PlaceId {
        override val value: Long = -1
    }

    data class Saved(override val value: Long) : PlaceId {
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
