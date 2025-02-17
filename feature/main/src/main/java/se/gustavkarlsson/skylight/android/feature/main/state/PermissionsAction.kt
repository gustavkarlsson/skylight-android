package se.gustavkarlsson.skylight.android.feature.main.state

import kotlinx.coroutines.flow.collectLatest
import me.tatarka.inject.annotations.Inject
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.AtomicStateFlow
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionChecker

@Inject
internal class PermissionsAction(
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
