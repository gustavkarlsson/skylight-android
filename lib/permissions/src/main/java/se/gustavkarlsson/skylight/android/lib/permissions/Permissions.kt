package se.gustavkarlsson.skylight.android.lib.permissions

import se.gustavkarlsson.skylight.android.lib.permissions.Permission.BackgroundLocation
import se.gustavkarlsson.skylight.android.lib.permissions.Permission.Location

class Permissions internal constructor(
    internal val map: Map<Permission, Access>,
) {
    init {
        val requiredKeys = Permission.values().asList()
        val missingKeys = requiredKeys - map.keys
        require(missingKeys.isEmpty()) {
            "Keys missing: $missingKeys"
        }
    }

    operator fun get(permission: Permission): Access {
        return checkNotNull(map[permission])
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Permissions

        if (map != other.map) return false

        return true
    }

    override fun hashCode(): Int {
        return map.hashCode()
    }

    override fun toString(): String {
        return "Permissions(map=$map)"
    }
}

internal fun Permissions.update(permission: Permission, newAccess: Access): Permissions {
    val newMap = map.toMutableMap()
    newMap[permission] = newAccess
    if (permission == Location && (newAccess == Access.Denied || newAccess == Access.DeniedForever)) {
        newMap[BackgroundLocation] = newAccess
    } else if (permission == BackgroundLocation && newAccess == Access.Granted) {
        newMap[Location] = newAccess
    }
    return Permissions(newMap)
}
