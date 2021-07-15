package se.gustavkarlsson.skylight.android.lib.kpindex

import kotlinx.coroutines.flow.Flow
import se.gustavkarlsson.skylight.android.core.entities.Loadable

interface KpIndexProvider {
    suspend fun get(fresh: Boolean = false): KpIndexReport
    fun stream(): Flow<Loadable<KpIndexReport>>
}
