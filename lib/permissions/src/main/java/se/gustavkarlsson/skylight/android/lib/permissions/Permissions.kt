package se.gustavkarlsson.skylight.android.lib.permissions

import co.selim.goldfinch.annotation.GenerateProperties

// TODO Upgrade to later version and add visibility argument when running Kotlin 1.5
@GenerateProperties
data class Permissions(
    val location: Permission.Location,
) {
    companion object {
        val INITIAL: Permissions = Permissions(
            location = Permission.Location.Unknown,
        )
    }
}

internal fun Permissions.update(permission: Permission): Permissions {
    return when (permission) {
        is Permission.Location -> copy(location = permission)
    }
}
