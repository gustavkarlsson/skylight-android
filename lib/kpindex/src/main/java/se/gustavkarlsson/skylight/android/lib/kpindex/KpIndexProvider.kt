package se.gustavkarlsson.skylight.android.lib.kpindex

import kotlinx.coroutines.flow.Flow
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.entities.Report

interface KpIndexProvider {
    suspend fun get(fresh: Boolean = false): Report<KpIndex>
    fun stream(): Flow<Loadable<Report<KpIndex>>>
}
