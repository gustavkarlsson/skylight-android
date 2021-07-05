package se.gustavkarlsson.skylight.android.feature.main.state

import com.ioki.textref.TextRef
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.threeten.bp.Duration
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.AtomicStateFlow
import se.gustavkarlsson.skylight.android.core.utils.throttle
import se.gustavkarlsson.skylight.android.feature.main.R
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.SearchThrottle
import se.gustavkarlsson.skylight.android.lib.geocoder.Geocoder
import se.gustavkarlsson.skylight.android.lib.geocoder.GeocodingResult
import se.gustavkarlsson.skylight.android.lib.geocoder.PlaceSuggestion
import se.gustavkarlsson.skylight.android.lib.ui.compose.SearchFieldState
import javax.inject.Inject
import kotlin.time.ExperimentalTime

internal class ContinuouslySearchAction @Inject constructor(
    private val searchChannel: ReceiveChannel<@JvmSuppressWildcards SearchFieldState>,
    private val geocoder: Geocoder,
    @SearchThrottle private val queryThrottleDuration: Duration,
) : Action<State> {
    @OptIn(
        ExperimentalTime::class,
        FlowPreview::class
    )
    override suspend fun execute(stateFlow: AtomicStateFlow<State>): Unit = coroutineScope {
        launch {
            searchChannel.consumeEach { searchFieldState ->
                stateFlow.update {
                    updateSearch(searchFieldState)
                }
            }
        }

        stateFlow
            .map { it.search }
            .distinctUntilChangedBy { search ->
                if (search is Search.Active) {
                    search.query.trim()
                } else null
            }
            .filterIsInstance<Search.Active>()
            .map { search -> search.query.trim() }
            .filter { query -> query.isNotBlank() }
            .throttle(queryThrottleDuration.toMillis())
            .collectLatest { query ->
                // TODO Ensure no race condition. Are these launches cancelled when a new query is collected?
                launch {
                    val result = geocoder.geocode(query)
                    stateFlow.update { update(result) }
                }
            }
    }

    private fun State.updateSearch(searchFieldState: SearchFieldState): State {
        val newSearch = when (searchFieldState) {
            SearchFieldState.Inactive -> Search.Inactive
            is SearchFieldState.Active -> {
                val query = searchFieldState.text
                if (query.isBlank()) {
                    Search.Active.Blank(query)
                } else {
                    when (val search = search) {
                        Search.Inactive, is Search.Active.Blank -> {
                            Search.Active.Filled(query, emptyList())
                        }
                        is Search.Active.Error -> search.copy(query)
                        is Search.Active.Filled -> search.copy(query)
                    }
                }
            }
        }
        return when (this) {
            is State.Loading -> copy(search = newSearch)
            is State.Ready -> copy(search = newSearch)
        }
    }

    private fun State.update(result: GeocodingResult): State {
        val search = search
        if (search !is Search.Active) return this
        val newSearch = when (result) {
            is GeocodingResult.Success -> {
                val suggestions = result.suggestions
                search.update(suggestions)
            }
            is GeocodingResult.Failure -> {
                val error = when (result) {
                    GeocodingResult.Failure.Io -> TextRef.stringRes(R.string.place_search_failed_io)
                    GeocodingResult.Failure.Server -> TextRef.stringRes(R.string.place_search_failed_server_response)
                    GeocodingResult.Failure.Unknown -> TextRef.stringRes(R.string.place_search_failed_generic)
                }
                search.update(search.suggestions, error)
            }
        }
        return when (this) {
            is State.Loading -> copy(search = newSearch)
            is State.Ready -> copy(search = newSearch)
        }
    }

    private fun Search.Active.update(
        suggestions: List<PlaceSuggestion>,
        errorText: TextRef? = null,
    ): Search {
        if (this is Search.Active.Blank) return this
        return when {
            errorText != null -> Search.Active.Error(query, errorText)
            else -> Search.Active.Filled(query, suggestions)
        }
    }
}
