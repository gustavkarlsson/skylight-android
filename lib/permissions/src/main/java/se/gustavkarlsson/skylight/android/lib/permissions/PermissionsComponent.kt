package se.gustavkarlsson.skylight.android.lib.permissions

import com.squareup.anvil.annotations.ContributesTo
import se.gustavkarlsson.skylight.android.core.AppScopeMarker

@ContributesTo(AppScopeMarker::class)
interface PermissionsComponent {

    fun permissionChecker(): PermissionChecker

    fun permissionRequester(): PermissionRequester

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
