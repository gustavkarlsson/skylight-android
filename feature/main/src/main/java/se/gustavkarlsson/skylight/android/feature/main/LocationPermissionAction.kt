package se.gustavkarlsson.skylight.android.feature.main

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.StateAccess
import se.gustavkarlsson.skylight.android.lib.permissions.Access

internal class LocationPermissionAction(
    private val permissionAccess: Flow<Access>
) : Action<State> {
    @InternalCoroutinesApi
    override suspend fun execute(stateAccess: StateAccess<State>) {
        permissionAccess.collect { access ->
            stateAccess.update { state ->
                state.copy(locationAccess = access)
            }
        }
    }
}
