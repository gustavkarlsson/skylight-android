package se.gustavkarlsson.skylight.android.lib.location

import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import com.dropbox.android.external.store4.fresh
import com.dropbox.android.external.store4.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.logging.logError
import se.gustavkarlsson.skylight.android.core.logging.logInfo

internal class StoreLocationProvider(
    private val store: Store<Unit, LocationResult>,
) : LocationProvider {
    override suspend fun get(fresh: Boolean): LocationResult {
        val result = if (fresh) {
            store.fresh(Unit)
        } else {
            store.get(Unit)
        }
        logInfo { "Provided location: $result" }
        return result
    }

    override fun stream(): Flow<Loadable<LocationResult>> =
        store.stream(StoreRequest.cached(Unit, refresh = false))
            .map { response ->
                when (response) {
                    is StoreResponse.Loading -> Loadable.loading()
                    is StoreResponse.Data -> Loadable.loaded(response.value)
                    is StoreResponse.Error.Exception -> {
                        logError(response.error) { "Failed to get location request" }
                        Loadable.loaded(LocationResult.errorUnknown())
                    }
                    is StoreResponse.Error.Message, is StoreResponse.NoNewData ->
                        error("Unsupported response type: $response")
                }
            }
            .distinctUntilChanged()
            .onEach { logInfo { "Streamed location: $it" } }
}
