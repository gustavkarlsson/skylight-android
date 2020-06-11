package se.gustavkarlsson.skylight.android.lib.permissions

interface PermissionsComponent {

    fun permissionChecker(): PermissionChecker

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
