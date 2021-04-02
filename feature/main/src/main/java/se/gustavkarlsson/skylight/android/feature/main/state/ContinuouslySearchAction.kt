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
                if (search is Search.Open) {
                    val searchQuery = search.query.trim()
                    val suggestionsQuery = search.suggestions.query.trim()
                    if (searchQuery != suggestionsQuery) {
                        val result = geocoder.geocode(searchQuery)
                        state.update { update(searchQuery, result) }
                    }
                }
            }
            val delayMillis = querySampleDelay.toMillis() - durationMillis
            delay(delayMillis)
        }
    }

    private fun State.update(searchQuery: String, result: GeocodingResult): State {
        if (search !is Search.Open) return this
        return when (result) {
            is GeocodingResult.Success -> {
                val suggestions = Suggestions(searchQuery, result.suggestions)
                updateState(search, suggestionsToSet = suggestions)
            }
            GeocodingResult.Failure.Io -> {
                val error = TextRef.stringRes(R.string.place_search_failed_io)
                updateState(search, errorToSet = error)
            }
            GeocodingResult.Failure.ServerError -> {
                val error = TextRef.stringRes(R.string.place_search_failed_server_response)
                updateState(search, errorToSet = error)
            }
            GeocodingResult.Failure.Unknown -> {
                val error = TextRef.stringRes(R.string.place_search_failed_generic)
                updateState(search, errorToSet = error)
            }
        }
    }

    private fun State.updateState(
        search: Search.Open,
        suggestionsToSet: Suggestions? = null,
        errorToSet: TextRef? = null,
    ): State {
        val suggestions = suggestionsToSet ?: search.suggestions
        val error = errorToSet ?: search.error
        return copy(search = search.copy(suggestions = suggestions, error = error))
    }
}
