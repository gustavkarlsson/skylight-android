package se.gustavkarlsson.skylight.android.lib.permissions

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.Reusable
import kotlinx.coroutines.flow.MutableStateFlow

@Module
object LibPermissionsModule {

    private val state: MutableStateFlow<Permissions> = MutableStateFlow(Permissions.INITIAL)

    @Provides
    @Reusable
    internal fun locationPermissionChecker(
        context: Context,
    ): PermissionChecker = AndroidPermissionChecker(context, state)

    @Provides
    @Reusable
    internal fun locationPermissionRequester(): PermissionRequester = RuntimePermissionRequester(state)
}

// TODO Show dialog if background permission not given but notifications are on.
