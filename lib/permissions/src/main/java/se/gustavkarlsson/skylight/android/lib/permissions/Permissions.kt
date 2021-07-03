package se.gustavkarlsson.skylight.android.lib.permissions

import se.gustavkarlsson.skylight.android.lib.permissions.Access.Denied
import se.gustavkarlsson.skylight.android.lib.permissions.Access.DeniedForever
import se.gustavkarlsson.skylight.android.lib.permissions.Access.Granted
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
    val permissionsToUpdate = getPermissionsWithKey(permission.key).toMutableSet()
    if (permission == Location && (newAccess == Denied || newAccess == DeniedForever)) {
        permissionsToUpdate += BackgroundLocation
    } else if (permission == BackgroundLocation && newAccess == Granted) {
        permissionsToUpdate += Location
    }
    val newMap = map.toMutableMap()
    for (p in permissionsToUpdate) {
        newMap[p] = newAccess
    }
    return Permissions(newMap)
}

fun getPermissionsWithKey(key: String): Collection<Permission> {
    return Permission.values()
        .filter { permission ->
            permission.key == key
        }
}
