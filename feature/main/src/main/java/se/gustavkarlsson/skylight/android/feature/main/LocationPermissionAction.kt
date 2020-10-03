package se.gustavkarlsson.skylight.android.feature.main

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.UpdateState
import se.gustavkarlsson.skylight.android.lib.permissions.Access

internal class LocationPermissionAction(
    private val permissionAccess: Flow<Access>
) : Action<State> {
    @InternalCoroutinesApi
    override suspend fun execute(updateState: UpdateState<State>) {
        permissionAccess.collect { access ->
            updateState { state ->
                state.copy(locationAccess = access)
            }
        }
    }
}
