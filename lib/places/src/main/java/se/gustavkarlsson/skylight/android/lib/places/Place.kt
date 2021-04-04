package se.gustavkarlsson.skylight.android.lib.places

import com.ioki.textref.TextRef
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.core.R
import se.gustavkarlsson.skylight.android.lib.location.Location

sealed class Place {
    abstract val id: Long
    abstract val displayName: TextRef

    object Current : Place() {
        override val id: Long = -1
        override val displayName = TextRef.stringRes(R.string.your_location)
    }

    sealed class Saved : Place() {
        abstract val location: Location
        abstract val name: String
        abstract val lastChanged: Instant

        data class Favorite(
            override val id: Long,
            override val name: String,
            override val location: Location,
            override val lastChanged: Instant,
        ) : Saved() {
            override val displayName get() = TextRef.string(name)
        }

        data class Recent(
            override val id: Long,
            override val name: String,
            override val location: Location,
            override val lastChanged: Instant,
        ) : Saved() {
            override val displayName get() = TextRef.string(name)
        }
    }
}
