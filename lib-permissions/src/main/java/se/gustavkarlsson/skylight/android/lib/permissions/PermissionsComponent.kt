package se.gustavkarlsson.skylight.android.lib.permissions

interface PermissionsComponent {

    fun permissionChecker(): PermissionChecker

    fun locationPermissionRequester(): PermissionRequester

    interface Setter {
        fun setPermissionsComponent(component: PermissionsComponent) {
            instance = component
        }
    }

    companion object {
        lateinit var instance: PermissionsComponent
            private set
    }
}
