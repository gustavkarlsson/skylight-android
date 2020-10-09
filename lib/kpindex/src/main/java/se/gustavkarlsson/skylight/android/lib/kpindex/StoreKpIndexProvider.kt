package se.gustavkarlsson.skylight.android.lib.kpindex

import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import com.dropbox.android.external.store4.fresh
import com.dropbox.android.external.store4.get
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.entities.Report
import se.gustavkarlsson.skylight.android.core.logging.logInfo

internal class StoreKpIndexProvider(
    private val store: Store<Unit, Report<KpIndex>>,
) : KpIndexProvider {

    override suspend fun get(fresh: Boolean): Report<KpIndex> =
        getSingleReport {
            if (fresh) {
                fresh(Unit)
            } else {
                get(Unit)
            }
        }

    private suspend fun getSingleReport(
        getKpIndex: suspend Store<Unit, Report<KpIndex>>.() -> Report<KpIndex>
    ): Report<KpIndex> {
        val report = store.getKpIndex()
        logInfo { "Provided Kp index: $report" }
        return report
    }

    @ExperimentalCoroutinesApi
    override fun stream(): Flow<Loadable<Report<KpIndex>>> =
        streamReports()
            .distinctUntilChanged()
            .onEach { logInfo { "Streamed Kp index: $it" } }

    private fun streamReports(): Flow<Loadable<Report<KpIndex>>> =
        store.stream(StoreRequest.cached(Unit, refresh = false))
            .map { response ->
                when (response) {
                    is StoreResponse.Loading -> Loadable.loading()
                    is StoreResponse.Data -> Loadable.loaded(response.value)
                    is StoreResponse.Error.Exception, is StoreResponse.Error.Message, is StoreResponse.NoNewData ->
                        error("Unsupported response type: $response")
                }
            }
}
