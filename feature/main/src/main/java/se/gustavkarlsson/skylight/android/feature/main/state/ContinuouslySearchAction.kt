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
                        state.update {
                            val newSearch = when (search) {
                                is Search.Active.Blank -> search
                                is Search.Active.Failure -> search.copy(inProgress = true)
                                is Search.Active.Success -> search.copy(inProgress = true)
                            }
                            copy(search = newSearch)
                        }
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
        error: TextRef? = null,
    ): Search {
        val inProgress = query.trim() != resultQuery.trim()
        return when {
            error != null -> Search.Active.Failure(query, inProgress, errorQuery = resultQuery, error)
            query.isBlank() -> Search.Active.Blank(query)
            else -> Search.Active.Success(query, inProgress, suggestions)
        }
    }
}
