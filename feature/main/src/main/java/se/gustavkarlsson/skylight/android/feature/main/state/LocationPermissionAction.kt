package se.gustavkarlsson.skylight.android.feature.main.state

import javax.inject.Inject
import kotlinx.coroutines.flow.collect
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.UpdatableStateFlow
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionChecker

internal class LocationPermissionAction @Inject constructor(
    private val permissionChecker: PermissionChecker,
) : Action<State> {
    override suspend fun execute(state: UpdatableStateFlow<State>) {
        permissionChecker.access.collect { access ->
            state.update {
                copy(locationAccess = access)
            }
        }
    }
}
