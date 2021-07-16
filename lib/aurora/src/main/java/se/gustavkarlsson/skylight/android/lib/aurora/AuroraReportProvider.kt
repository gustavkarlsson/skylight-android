package se.gustavkarlsson.skylight.android.lib.aurora

import kotlinx.coroutines.flow.Flow
import se.gustavkarlsson.skylight.android.lib.location.Location

interface AuroraReportProvider {
    suspend fun get(location: Location): CompleteAuroraReport
    fun stream(location: Location): Flow<LoadableAuroraReport>
}
