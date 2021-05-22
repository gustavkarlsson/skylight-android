package se.gustavkarlsson.skylight.android.lib.permissions

// TODO rework to Map<Permission, Access> and require function?
//  Location and backgroud location need to be combined, as they have mutually exclusive states
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
                    location = Permission.Location.withDefaultAccess(),
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
    return Permissions(
        location = location.update(newValues),
        backgroundLocation = backgroundLocation.update(newValues),
    )
}

private fun PermissionAccess.update(newValues: List<PermissionAccess>): PermissionAccess {
    val newAccess = newValues
        .find { newPermissionAccess ->
            newPermissionAccess.permission == permission
        }?.access ?: return this
    return copy(access = newAccess)
}
