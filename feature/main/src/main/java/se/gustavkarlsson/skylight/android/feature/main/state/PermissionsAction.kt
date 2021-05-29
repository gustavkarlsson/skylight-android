package se.gustavkarlsson.skylight.android.feature.main.state

import kotlinx.coroutines.flow.collect
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.AtomicStateFlow
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionChecker
import javax.inject.Inject

internal class PermissionsAction @Inject constructor(
    private val permissionChecker: PermissionChecker,
) : Action<State> {
    override suspend fun execute(state: AtomicStateFlow<State>) {
        permissionChecker.permissions.collect { permissions ->
            state.update {
                copy(permissions = permissions)
            }
        }
    }
}
