package se.gustavkarlsson.skylight.android.feature.main.state

import com.ioki.textref.TextRef
import javax.inject.Inject
import kotlin.system.measureTimeMillis
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.delay
import org.threeten.bp.Duration
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.UpdatableStateFlow
import se.gustavkarlsson.skylight.android.feature.main.R
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.SearchDelay
import se.gustavkarlsson.skylight.android.lib.geocoder.Geocoder
import se.gustavkarlsson.skylight.android.lib.geocoder.GeocodingResult

// TODO Rework to use flow of search queries
internal class ContinuouslySearchAction @Inject constructor(
    private val geocoder: Geocoder,
    @SearchDelay private val querySampleDelay: Duration,
) : Action<State> {
    @ExperimentalTime
    override suspend fun execute(state: UpdatableStateFlow<State>) {
        while (true) {
            val search = state.value.search
            val durationMillis = measureTimeMillis {
                if (search is Search.Active) {
                    val searchQuery = search.query.trim()
                    val suggestionsQuery = search.suggestions.query.trim()
                    if (searchQuery != suggestionsQuery) {
                        val result = geocoder.geocode(searchQuery)
                        state.update { update(result, searchQuery) }
                    }
                }
            }
            val delayMillis = querySampleDelay.toMillis() - durationMillis
            delay(delayMillis)
        }
    }

    private fun State.update(result: GeocodingResult, searchQuery: String): State {
        if (search !is Search.Active) return this
        val newSearch = when (result) {
            is GeocodingResult.Success -> {
                val suggestions = Suggestions(searchQuery, result.suggestions)
                search.update(searchQuery, suggestions)
            }
            is GeocodingResult.Failure -> {
                val error = when (result) {
                    GeocodingResult.Failure.Io -> TextRef.stringRes(R.string.place_search_failed_io)
                    GeocodingResult.Failure.Server -> TextRef.stringRes(R.string.place_search_failed_server_response)
                    GeocodingResult.Failure.Unknown -> TextRef.stringRes(R.string.place_search_failed_generic)
                }
                search.update(searchQuery, search.suggestions, error)
            }
        }
        return copy(search = newSearch)
    }

    private fun Search.Active.update(
        resultQuery: String,
        suggestions: Suggestions,
        errorText: TextRef? = null,
    ): Search {
        if (this is Search.Active.Blank) return this
        return when {
            errorText != null -> Search.Active.Error(query, errorQuery = resultQuery, errorText)
            else -> Search.Active.Filled(query, suggestions)
        }
    }
}
