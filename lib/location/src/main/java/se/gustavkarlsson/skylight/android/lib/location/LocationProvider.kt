package se.gustavkarlsson.skylight.android.lib.location

import kotlinx.coroutines.flow.Flow
import se.gustavkarlsson.skylight.android.core.entities.Loadable

interface LocationProvider {
    suspend fun get(fresh: Boolean = false): LocationResult
    fun stream(): Flow<Loadable<LocationResult>>
}
