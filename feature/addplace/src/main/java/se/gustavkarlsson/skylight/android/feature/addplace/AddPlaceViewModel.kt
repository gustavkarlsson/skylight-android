package se.gustavkarlsson.skylight.android.feature.addplace

import com.ioki.textref.TextRef
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import se.gustavkarlsson.conveyor.Store
import se.gustavkarlsson.skylight.android.lib.geocoder.PlaceSuggestion
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.places.PlacesRepository
import se.gustavkarlsson.skylight.android.lib.ui.CoroutineScopedService
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
internal class AddPlaceViewModel @Inject constructor(
    private val placesRepository: PlacesRepository,
    private val store: Store<State>,
    val errorMessages: Flow<TextRef>,
) : CoroutineScopedService() {

    init { store.start(scope) }

    private val stateFlow = store.state

    private val navigateAwayChannel = BroadcastChannel<Unit>(Channel.BUFFERED)
    val navigateAway: Flow<Unit> = navigateAwayChannel.asFlow()

    private val openSaveDialogChannel = BroadcastChannel<PlaceSuggestion>(Channel.BUFFERED)
    val openSaveDialog: Flow<PlaceSuggestion> = openSaveDialogChannel.asFlow()

    fun onSearchTextChanged(newText: String) = store.issue(SetQueryAction(newText))

    val placeSuggestions: Flow<List<SuggestionItem>> = stateFlow
        .map { it.suggestions.items }
        .distinctUntilChanged()
        .filter { it.isNotEmpty() }
        .map { suggestions ->
            suggestions.map {
                SuggestionItem(createTitle(it), createSubtitle(it), it)
            }
        }

    fun onSuggestionClicked(suggestion: PlaceSuggestion) = openSaveDialogChannel.offer(suggestion)

    suspend fun onSavePlaceClicked(name: String, location: Location) {
        placesRepository.add(name, location)
        navigateAwayChannel.offer(Unit)
    }

    private val resultState: Flow<ResultState> = stateFlow
        .flatMapLatest { state ->
            when {
                state.query.isBlank() -> flowOf(ResultState.EMPTY)
                !state.isSuggestionsUpToDate && state.suggestions.items.isEmpty() ->
                    flow {
                        delay(1000)
                        emit(ResultState.SEARCHING)
                    }
                state.suggestions.items.isEmpty() -> flowOf(ResultState.NO_SUGGESTIONS)
                else -> flowOf(ResultState.SUGGESTIONS)
            }
        }
        .distinctUntilChanged()

    val isEmptyVisible: Flow<Boolean> =
        resultState.map { it == ResultState.EMPTY }

    val isSearchingVisible: Flow<Boolean> =
        resultState.map { it == ResultState.SEARCHING }

    val isNoSuggestionsVisible: Flow<Boolean> =
        resultState.map { it == ResultState.NO_SUGGESTIONS }

    val isSuggestionsVisible: Flow<Boolean> =
        resultState.map { it == ResultState.SUGGESTIONS }
}

private fun createTitle(suggestion: PlaceSuggestion) = suggestion.simpleName

private fun createSubtitle(suggestion: PlaceSuggestion) =
    suggestion.fullName
        .removePrefix(suggestion.simpleName)
        .dropWhile { !it.isLetterOrDigit() }

internal data class SuggestionItem(
    val textLine1: String,
    val textLine2: String,
    val suggestion: PlaceSuggestion
)

internal enum class ResultState {
    EMPTY, SEARCHING, NO_SUGGESTIONS, SUGGESTIONS
}
