package se.gustavkarlsson.skylight.android.feature.main.state

import kotlinx.coroutines.flow.collectLatest
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.AtomicStateFlow
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionChecker
import javax.inject.Inject

internal class PermissionsAction @Inject constructor(
    private val permissionChecker: PermissionChecker,
) : Action<State> {
    override suspend fun execute(stateFlow: AtomicStateFlow<State>) {
        permissionChecker.permissions.collectLatest { permissions ->
            stateFlow.update {
                when (this) {
                    is State.Loading -> copy(permissions = permissions)
                    is State.Ready -> copy(permissions = permissions)
                }
            }
        }
    }
}
