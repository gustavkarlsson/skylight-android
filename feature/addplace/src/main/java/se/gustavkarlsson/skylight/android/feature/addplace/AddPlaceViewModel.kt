package se.gustavkarlsson.skylight.android.feature.addplace

import com.ioki.textref.TextRef
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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
        .map { state -> state.toViewState() }
        .stateIn(scope, SharingStarted.Eagerly, stateFlow.value.toViewState())

    private fun State.toViewState(): ViewState {
        val suggestions = when {
            suggestions.items.isNotEmpty() -> {
                suggestions.items.map { suggestion ->
                    suggestion.toSuggestionItem()
                }
            }
            isSuggestionsUpToDate -> emptyList()
            else -> null
        }

        return ViewState(
            query = query,
            searching = !isSuggestionsUpToDate,
            suggestions = suggestions,
            error = error,
        )
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

internal data class ViewState(
    val query: String = "",
    val searching: Boolean = false,
    val suggestions: List<SuggestionItem>? = null,
    val error: TextRef? = null,
)

private fun PlaceSuggestion.toSuggestionItem(): SuggestionItem {
    return SuggestionItem(
        primaryText = simpleName,
        secondaryText = createSubtitle(this),
        suggestedName = simpleName,
        location = location,
    )
}

private fun createSubtitle(suggestion: PlaceSuggestion) =
    suggestion.fullName
        .removePrefix(suggestion.simpleName)
        .dropWhile { !it.isLetterOrDigit() }

internal data class SuggestionItem(
    val primaryText: String,
    val secondaryText: String,
    val suggestedName: String,
    val location: Location,
)
