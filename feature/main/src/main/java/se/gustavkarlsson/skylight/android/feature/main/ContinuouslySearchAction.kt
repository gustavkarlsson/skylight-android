package se.gustavkarlsson.skylight.android.feature.main

import com.ioki.textref.TextRef
import kotlin.system.measureTimeMillis
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.delay
import org.threeten.bp.Duration
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.UpdatableStateFlow
import se.gustavkarlsson.skylight.android.lib.geocoder.Geocoder
import se.gustavkarlsson.skylight.android.lib.geocoder.GeocodingResult

internal class ContinuouslySearchAction(
    private val geocoder: Geocoder,
    private val querySampleDelay: Duration,
) : Action<State> {
    @ExperimentalTime
    override suspend fun execute(state: UpdatableStateFlow<State>) {
        while (true) {
            val search = state.value.search
            val durationMillis = measureTimeMillis {
                if (search is Search.Open && search.query != search.suggestions.query) {
                    val result = geocoder.geocode(search.query)
                    state.update { update(search.query, result) }
                }
            }
            val delayMillis = querySampleDelay.toMillis() - durationMillis
            delay(delayMillis)
        }
    }

    private fun State.update(requestQuery: String, result: GeocodingResult): State {
        if (search !is Search.Open) return this
        return when (result) {
            is GeocodingResult.Success -> {
                val suggestions = Suggestions(requestQuery, result.suggestions)
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
