package se.gustavkarlsson.skylight.android.feature.addplace

import com.ioki.textref.TextRef
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import se.gustavkarlsson.conveyor.Store
import se.gustavkarlsson.conveyor.issue
import se.gustavkarlsson.skylight.android.lib.geocoder.PlaceSuggestion
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.places.PlacesRepository
import se.gustavkarlsson.skylight.android.lib.ui.CoroutineScopedService

@FlowPreview
@ExperimentalCoroutinesApi
internal class AddPlaceViewModel @Inject constructor(
    private val placesRepository: PlacesRepository,
    private val store: Store<State>,
) : CoroutineScopedService() {

    init {
        store.start(scope)
    }

    private val stateFlow = store.state

    private val navigateAwayChannel = BroadcastChannel<Unit>(Channel.BUFFERED)
    val navigateAway: Flow<Unit> = navigateAwayChannel.asFlow()

    val viewState = stateFlow
        .map { state ->
            val resultState = when {
                state.query.isBlank() -> ResultState.EMPTY
                state.isSearchingForQuery && state.suggestions.items.isEmpty() -> ResultState.SEARCHING
                state.suggestions.items.isEmpty() -> ResultState.NO_SUGGESTIONS
                else -> ResultState.SUGGESTIONS
            }

            when (resultState) {
                ResultState.EMPTY -> ViewState.Empty(error = state.error)
                ResultState.SEARCHING -> ViewState.Searching(state.query, error = state.error)
                ResultState.NO_SUGGESTIONS -> ViewState.NoSuggestions(state.query, error = state.error)
                ResultState.SUGGESTIONS -> {
                    val suggestions = state.suggestions.items.map { suggestion ->
                        val title = createTitle(suggestion)
                        SuggestionItem(
                            primaryText = title,
                            secondaryText = createSubtitle(suggestion),
                            saveName = title,
                            location = suggestion.location,
                        )
                    }
                    ViewState.Suggestions(
                        query = state.query,
                        suggestions = suggestions,
                        error = state.error,
                    )
                }
            }
        }

    fun onSearchTextChanged(newText: String) = store.issue(SetQueryAction(newText))

    fun onSnackbarDismissed() = store.issue { state ->
        state.update {
            copy(error = null)
        }
    }

    fun onSavePlaceClicked(name: String, location: Location) {
        scope.launch {
            placesRepository.add(name, location)
            navigateAwayChannel.offer(Unit)
        }
    }
}

internal sealed class ViewState {
    abstract val query: String
    abstract val error: TextRef?

    data class Empty(override val error: TextRef?) : ViewState() {
        override val query = ""
    }

    data class Searching(override val query: String, override val error: TextRef?) : ViewState()
    data class NoSuggestions(override val query: String, override val error: TextRef?) : ViewState()
    data class Suggestions(
        override val query: String,
        val suggestions: List<SuggestionItem>,
        override val error: TextRef?,
    ) : ViewState()

    companion object {
        val default: Empty = Empty(error = null)
    }
}

private fun createTitle(suggestion: PlaceSuggestion) = suggestion.simpleName

private fun createSubtitle(suggestion: PlaceSuggestion) =
    suggestion.fullName
        .removePrefix(suggestion.simpleName)
        .dropWhile { !it.isLetterOrDigit() }

internal data class SuggestionItem(
    val primaryText: String,
    val secondaryText: String,
    val saveName: String,
    val location: Location,
)

internal enum class ResultState {
    EMPTY, SEARCHING, NO_SUGGESTIONS, SUGGESTIONS
}
