package se.gustavkarlsson.skylight.android.feature.main

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.UpdatableStateFlow
import se.gustavkarlsson.skylight.android.lib.permissions.Access

internal class LocationPermissionAction(
    private val permissionAccess: Flow<Access>
) : Action<State> {
    override suspend fun execute(state: UpdatableStateFlow<State>) {
        permissionAccess.collect { access ->
            state.update {
                copy(locationAccess = access)
            }
        }
    }
}
