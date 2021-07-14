package se.gustavkarlsson.skylight.android.lib.kpindex

import com.dropbox.android.external.store4.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import se.gustavkarlsson.skylight.android.core.entities.*
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.lib.time.Time
import java.io.IOException

internal class StoreKpIndexProvider(
    private val store: Store<Unit, KpIndex>,
    private val time: Time
) : KpIndexProvider {

    override suspend fun get(fresh: Boolean): Report<KpIndex> {
        val report = getReport(fresh)
        logInfo { "Provided Kp index: $report" }
        return report
    }

    private suspend fun getReport(fresh: Boolean): Report<KpIndex> {
        return try {
            val kpIndex = if (fresh) {
                store.fresh(Unit)
            } else store.get(Unit)
            Report.Success(kpIndex, time.now())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Report.Error(getCause(e), time.now())
        }
    }

    override fun stream(): Flow<Loadable<Report<KpIndex>>> =
        streamReports()
            .distinctUntilChanged()
            .onEach { logInfo { "Streamed Kp index: $it" } }

    private fun streamReports(): Flow<Loadable<Report<KpIndex>>> =
        store.stream(StoreRequest.cached(Unit, refresh = false))
            .map { response ->
                when (response) {
                    is StoreResponse.Loading -> Loading
                    is StoreResponse.Data -> Loaded(Report.success(response.value, time.now()))
                    is StoreResponse.Error.Exception ->
                        Loaded(Report.error(getCause(response.error), time.now()))
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
