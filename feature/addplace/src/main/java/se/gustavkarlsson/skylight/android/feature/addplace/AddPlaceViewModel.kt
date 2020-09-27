package se.gustavkarlsson.skylight.android.feature.addplace

import com.ioki.textref.TextRef
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.rx2.asFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import se.gustavkarlsson.skylight.android.lib.geocoder.PlaceSuggestion
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.places.PlacesRepository
import se.gustavkarlsson.skylight.android.lib.scopedservice.ScopedService
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
internal class AddPlaceViewModel @Inject constructor(
    private val placesRepository: PlacesRepository,
    private val knot: AddPlaceKnot,
    val errorMessages: Flow<TextRef>
) : ScopedService {

    // TODO move to base ScopedService?
    private val scope = CoroutineScope(SupervisorJob()) + CoroutineName("AddPlaceViewModel scope")

    init {
        scope.launch {
            suspendCancellableCoroutine { continuation ->
                continuation.invokeOnCancellation {
                    knot.dispose()
                }
            }
        }
    }

    override fun onCleared() {
        scope.cancel("ViewModel cleared")
    }

    private val stateFlow = knot.state.asFlow()

    private val navigateAwayChannel = BroadcastChannel<Unit>(Channel.BUFFERED)
    val navigateAway: Flow<Unit> = navigateAwayChannel.asFlow()

    private val openSaveDialogChannel = BroadcastChannel<PlaceSuggestion>(Channel.BUFFERED)
    val openSaveDialog: Flow<PlaceSuggestion> = openSaveDialogChannel.asFlow()

    fun onSearchTextChanged(newText: String) = knot.change.accept(Change.Query(newText))

    val placeSuggestions: Flow<List<SuggestionItem>> = stateFlow
        .map { it.suggestions }
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
        .mapNotNull { state ->
            when {
                state.query.isBlank() -> ResultState.EMPTY
                state.isSearching && state.suggestions.isEmpty() -> ResultState.SEARCHING
                state.suggestions.isEmpty() -> ResultState.NO_SUGGESTIONS
                else -> ResultState.SUGGESTIONS
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
