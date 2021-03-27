package se.gustavkarlsson.skylight.android.lib.places

import com.ioki.textref.TextRef
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.core.R
import se.gustavkarlsson.skylight.android.lib.location.Location

sealed class Place {
    abstract val id: Long?
    abstract val name: TextRef
    abstract val nameString: String?
    abstract val location: Location?

    object Current : Place() {
        override val id: Nothing? = null
        override val name = TextRef.stringRes(R.string.your_location)
        override val nameString: String? = null
        override val location: Location? = null
    }

    // TODO let favorite and recent share parent class Elsewhere?
    data class Favorite(
        override val id: Long,
        override val nameString: String, // TODO rename?
        override val location: Location,
        val lastChanged: Instant,
    ) : Place() {
        override val name get() = TextRef.string(nameString)
    }

    data class Recent(
        override val id: Long,
        override val nameString: String, // TODO rename?
        override val location: Location,
        val lastChanged: Instant,
    ) : Place() {
        override val name get() = TextRef.string(nameString)
    }
}
