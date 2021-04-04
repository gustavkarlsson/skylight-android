package se.gustavkarlsson.skylight.android.lib.permissions

import se.gustavkarlsson.skylight.android.core.logging.logDebug

// FIXME rework to Map<Permission, Access> and require function?
data class Permissions(
    val location: PermissionAccess,
    val backgroundLocation: PermissionAccess,
) {
    fun toMap(): Map<Permission, Access> = listOf(location, backgroundLocation)
        .map { it.permission to it.access }
        .toMap()

    operator fun get(permission: Permission): Access {
        return toMap()[permission] ?: error("Failed to get access for permission: $permission")
    }

    companion object {
        val INITIAL: Permissions
            get() {
                return Permissions(
                    location = Permission.LocationFine.withDefaultAccess(),
                    backgroundLocation = Permission.BackgroundLocation.withDefaultAccess(),
                )
            }

        private fun Permission.withDefaultAccess(): PermissionAccess {
            val access = if (supported) {
                Access.Unknown
            } else Access.Granted
            return PermissionAccess(this, access)
        }
    }
}

internal fun Permissions.update(newValues: List<PermissionAccess>): Permissions {
    val newLocation = location.update(newValues)
    val newBackgroundLocation = backgroundLocation.update(newValues)
    return Permissions(
        location = newLocation,
        backgroundLocation = newBackgroundLocation,
    )
}

private fun PermissionAccess.update(newValues: List<PermissionAccess>): PermissionAccess {
    val newAccess = newValues
        .find { newPermissionAccess ->
            newPermissionAccess.permission == permission
        }?.access ?: return this

    val actualNewAccess = if (access == Access.DeniedForever && newAccess == Access.Denied) {
        logDebug { "Won't change $permission from $access to $newAccess" }
        access
    } else newAccess
    return copy(access = actualNewAccess)
}
