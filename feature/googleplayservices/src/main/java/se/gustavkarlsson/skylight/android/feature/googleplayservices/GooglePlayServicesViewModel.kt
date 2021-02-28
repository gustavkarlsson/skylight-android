package se.gustavkarlsson.skylight.android.feature.googleplayservices

import android.app.Activity
import com.google.android.gms.common.GoogleApiAvailability
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.Store
import se.gustavkarlsson.conveyor.UpdatableStateFlow
import se.gustavkarlsson.conveyor.issue
import se.gustavkarlsson.skylight.android.core.Main
import se.gustavkarlsson.skylight.android.core.logging.logError
import se.gustavkarlsson.skylight.android.lib.ui.CoroutineScopedService

internal class GooglePlayServicesViewModel @Inject constructor(
    @Main private val dispatcher: CoroutineDispatcher,
) : CoroutineScopedService() {

    private val store = Store<Install>(Install.Idle).apply {
        start(scope)
    }

    val error: Flow<Boolean> = store.state.map { state -> state is Install.Error }

    val success: Flow<Unit> = store.state.mapNotNull { state ->
        if (state is Install.Success) {
            Unit
        } else null
    }

    fun installGooglePlayServices(activity: Activity) {
        val action = MakeGooglePlayServicesAvailableAction(activity, dispatcher)
        store.issue(action)
    }

    fun clearError() {
        store.issue { state ->
            state.update { Install.Idle }
        }
    }
}

private sealed class Install {
    object Idle : Install()
    object Success : Install()
    object Error : Install()
}

private class MakeGooglePlayServicesAvailableAction(
    private val activity: Activity,
    private val mainDispatcher: CoroutineDispatcher,
) : Action<Install> {
    override suspend fun execute(state: UpdatableStateFlow<Install>) {
        val newState = withContext(mainDispatcher + CoroutineName("makeGooglePlayServicesAvailable")) {
            try {
                // TODO Extract to library module (together with GooglePlayServicesChecker)
                GoogleApiAvailability.getInstance()
                    .makeGooglePlayServicesAvailable(activity)
                    .await()
                Install.Success
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                logError(e) { "Failed to install Google Play Services" }
                Install.Error
            }
        }
        state.update { newState }
    }
}
