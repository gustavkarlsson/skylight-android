package se.gustavkarlsson.skylight.android.lib.permissions

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.Reusable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import se.gustavkarlsson.skylight.android.core.logging.logInfo

@Module
object LibPermissionsModule {

    @ExperimentalCoroutinesApi
    private val state: MutableStateFlow<Permissions> = MutableStateFlow(Permissions.INITIAL)

    @ExperimentalCoroutinesApi
    @Provides
    @Reusable
    internal fun locationPermissionChecker(
        context: Context
    ): PermissionChecker = AndroidPermissionChecker(context, state)

    @ExperimentalCoroutinesApi
    @Provides
    @Reusable
    internal fun locationPermissionRequester(): PermissionRequester =
        RuntimePermissionRequester { newPermissions ->
            val old = state.value
            val new = old.update(newPermissions)
            if (new != old) {
                state.value = new
                logInfo { "Permissions changed from $old to $new" }
            }
        }
}

// TODO Show dialog if background permission not given but notifications are on.
