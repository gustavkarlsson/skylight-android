package se.gustavkarlsson.skylight.android.feature.googleplayservices

import android.app.Activity
import com.google.android.gms.common.GoogleApiAvailability
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.AtomicStateFlow
import se.gustavkarlsson.conveyor.Store
import se.gustavkarlsson.conveyor.issue
import se.gustavkarlsson.skylight.android.core.Main
import se.gustavkarlsson.skylight.android.core.logging.logError
import se.gustavkarlsson.skylight.android.core.utils.mapState
import se.gustavkarlsson.skylight.android.lib.ui.CoroutineScopedService
import javax.inject.Inject

internal class GooglePlayServicesViewModel @Inject constructor(
    @Main private val dispatcher: CoroutineDispatcher,
) : CoroutineScopedService() {

    private val store = Store(Install.Idle).apply {
        start(scope)
    }

    val error: StateFlow<Boolean> = store.state.mapState { state -> state == Install.Error }

    val success: Flow<Unit> = store.state.mapNotNull { state ->
        if (state == Install.Success) {
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

private enum class Install {
    Idle, Success, Error
}

private class MakeGooglePlayServicesAvailableAction(
    private val activity: Activity,
    private val mainDispatcher: CoroutineDispatcher,
) : Action<Install> {
    override suspend fun execute(stateFlow: AtomicStateFlow<Install>) {
        val newState = withContext(mainDispatcher + CoroutineName("makeGooglePlayServicesAvailable")) {
            try {
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
        stateFlow.update { newState }
    }
}
