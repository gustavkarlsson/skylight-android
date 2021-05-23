package se.gustavkarlsson.skylight.android.lib.permissions

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.Reusable
import kotlinx.coroutines.flow.MutableStateFlow
import se.gustavkarlsson.skylight.android.core.logging.logInfo

@Module
object LibPermissionsModule {

    private val state: MutableStateFlow<Permissions> = MutableStateFlow(Permissions.INITIAL)

    @Provides
    @Reusable
    internal fun locationPermissionChecker(
        context: Context
    ): PermissionChecker = AndroidPermissionChecker(context, state)

    @Provides
    @Reusable
    internal fun locationPermissionRequester(): PermissionRequester =
        RuntimePermissionRequester { newPermissions ->
            val old = state.value
            val new = newPermissions.fold(old) { acc, permission ->
                acc.update(permission)
            }
            if (new != old) {
                state.value = new
                logInfo { "Permissions changed from $old to $new" }
            }
        }
}

// TODO Show dialog if background permission not given but notifications are on.
