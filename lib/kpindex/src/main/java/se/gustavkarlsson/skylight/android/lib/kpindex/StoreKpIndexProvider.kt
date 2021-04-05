package se.gustavkarlsson.skylight.android.lib.kpindex

import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import com.dropbox.android.external.store4.fresh
import com.dropbox.android.external.store4.get
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import se.gustavkarlsson.skylight.android.core.entities.Cause
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.entities.Report
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.lib.time.Time

internal class StoreKpIndexProvider(
    private val store: Store<Unit, KpIndex>,
    private val time: Time
) : KpIndexProvider {

    override suspend fun get(fresh: Boolean): Report<KpIndex> = getReport(fresh)

    private suspend fun getReport(fresh: Boolean): Report<KpIndex> {
        val report = try {
            val kpIndex = if (fresh) {
                store.fresh(Unit)
            } else store.get(Unit)
            Report.Success(kpIndex, time.now())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Report.Error(getCause(e), time.now())
        }
        logInfo { "Provided Kp index: $report" }
        return report
    }

    override fun stream(): Flow<Loadable<Report<KpIndex>>> =
        streamReports()
            .distinctUntilChanged()
            .onEach { logInfo { "Streamed Kp index: $it" } }

    private fun streamReports(): Flow<Loadable<Report<KpIndex>>> =
        store.stream(StoreRequest.cached(Unit, refresh = false))
            .map { response ->
                when (response) {
                    is StoreResponse.Loading -> Loadable.loading()
                    is StoreResponse.Data -> Loadable.loaded(Report.success(response.value, time.now()))
                    is StoreResponse.Error.Exception ->
                        Loadable.loaded(Report.error(getCause(response.error), time.now()))
                    is StoreResponse.Error.Message, is StoreResponse.NoNewData ->
                        error("Unsupported response type: $response")
                }
            }
}

// TODO Fix duplication with RetrofittedOpenWeatherMapWeatherProvider
private fun getCause(throwable: Throwable): Cause =
    when (throwable) {
        is IOException -> Cause.Connectivity
        is ServerResponseException -> Cause.ServerResponse
        else -> Cause.Unknown
    }
