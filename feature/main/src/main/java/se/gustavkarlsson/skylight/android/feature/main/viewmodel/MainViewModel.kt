package se.gustavkarlsson.skylight.android.feature.main.viewmodel

import androidx.annotation.StringRes
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import com.ioki.textref.TextRef
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import se.gustavkarlsson.conveyor.Store
import se.gustavkarlsson.conveyor.issue
import se.gustavkarlsson.koptional.optionalOf
import se.gustavkarlsson.skylight.android.core.entities.Cause
import se.gustavkarlsson.skylight.android.core.entities.Chance
import se.gustavkarlsson.skylight.android.core.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.entities.Report
import se.gustavkarlsson.skylight.android.core.entities.TriggerLevel
import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.core.services.Formatter
import se.gustavkarlsson.skylight.android.feature.main.R
import se.gustavkarlsson.skylight.android.feature.main.state.Search
import se.gustavkarlsson.skylight.android.feature.main.state.State
import se.gustavkarlsson.skylight.android.feature.main.state.Suggestions
import se.gustavkarlsson.skylight.android.feature.main.util.RelativeTimeFormatter
import se.gustavkarlsson.skylight.android.lib.aurora.CompleteAuroraReport
import se.gustavkarlsson.skylight.android.lib.darkness.Darkness
import se.gustavkarlsson.skylight.android.lib.geocoder.PlaceSuggestion
import se.gustavkarlsson.skylight.android.lib.geomaglocation.GeomagLocation
import se.gustavkarlsson.skylight.android.lib.kpindex.KpIndex
import se.gustavkarlsson.skylight.android.lib.permissions.Access
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionChecker
import se.gustavkarlsson.skylight.android.lib.places.Place
import se.gustavkarlsson.skylight.android.lib.places.PlacesRepository
import se.gustavkarlsson.skylight.android.lib.places.SelectedPlaceRepository
import se.gustavkarlsson.skylight.android.lib.reversegeocoder.ReverseGeocodingResult
import se.gustavkarlsson.skylight.android.lib.settings.Settings
import se.gustavkarlsson.skylight.android.lib.time.Time
import se.gustavkarlsson.skylight.android.lib.ui.CoroutineScopedService
import se.gustavkarlsson.skylight.android.lib.ui.compose.Icons
import se.gustavkarlsson.skylight.android.lib.ui.compose.SearchFieldState
import se.gustavkarlsson.skylight.android.lib.ui.compose.ToggleButtonState
import se.gustavkarlsson.skylight.android.lib.weather.Weather

@ExperimentalCoroutinesApi
internal class MainViewModel(
    private val store: Store<State>,
    private val placesRepository: PlacesRepository,
    private val selectedPlaceRepository: SelectedPlaceRepository,
    private val auroraChanceEvaluator: ChanceEvaluator<CompleteAuroraReport>,
    private val relativeTimeFormatter: RelativeTimeFormatter,
    private val chanceLevelFormatter: Formatter<ChanceLevel>,
    private val darknessChanceEvaluator: ChanceEvaluator<Darkness>,
    private val darknessFormatter: Formatter<Darkness>,
    private val geomagLocationChanceEvaluator: ChanceEvaluator<GeomagLocation>,
    private val geomagLocationFormatter: Formatter<GeomagLocation>,
    private val kpIndexChanceEvaluator: ChanceEvaluator<KpIndex>,
    private val kpIndexFormatter: Formatter<KpIndex>,
    private val weatherChanceEvaluator: ChanceEvaluator<Weather>,
    private val weatherFormatter: Formatter<Weather>,
    private val permissionChecker: PermissionChecker,
    private val time: Time,
    private val settings: Settings,
    private val nowTextThreshold: Duration
) : CoroutineScopedService() {

    init {
        store.start(scope)
    }

    val state: StateFlow<ViewState> = store.state
        .map { state -> state.toViewState() }
        .stateIn(scope, SharingStarted.Eagerly, store.state.value.toViewState())

    // FIXME clean up
    private fun State.toViewState(): ViewState {
        val state = this
        val changeLevelText = let {
            val chance = state.selectedAuroraReport.toCompleteAuroraReport()
                ?.let(auroraChanceEvaluator::evaluate)
                ?: Chance.UNKNOWN
            val level = ChanceLevel.fromChance(chance)
            chanceLevelFormatter.format(level)
        }
        val chanceSubtitleText = let {
            val name = optionalOf(state.selectedAuroraReport.locationName)
                .map { it as? Loadable.Loaded<ReverseGeocodingResult> }
                .map { it.value as? ReverseGeocodingResult.Success }
                .map { it.name }
                .value
            val relativeTime = state.selectedAuroraReport.timestamp?.let { timestamp ->
                // FIXME emit new time every sec
                relativeTimeFormatter.format(timestamp, time.now(), nowTextThreshold).toString()
            }
            when {
                relativeTime == null -> TextRef.EMPTY
                name == null -> TextRef.string(relativeTime)
                else -> TextRef.stringRes(R.string.time_in_location, relativeTime, name)
            }
        }
        val errorBannerData = when {
            state.selectedPlace != Place.Current -> null
            state.locationAccess == Access.Denied -> {
                BannerData(
                    TextRef.stringRes(R.string.location_permission_denied_message),
                    TextRef.stringRes(R.string.fix),
                    Icons.LocationOn,
                    BannerData.Event.RequestLocationPermission
                )
            }
            state.locationAccess == Access.DeniedForever -> {
                BannerData(
                    TextRef.stringRes(R.string.location_permission_denied_forever_message),
                    TextRef.stringRes(R.string.fix),
                    Icons.Warning,
                    BannerData.Event.OpenAppDetails
                )
            }
            else -> null
        }
        val favoriteButtonState = when (state.selectedPlace) {
            Place.Current -> ToggleButtonState.Gone
            is Place.Recent -> ToggleButtonState.Enabled(checked = false)
            is Place.Favorite -> ToggleButtonState.Enabled(checked = true)
        }
        val notificationChecked = selectedPlaceTriggerLevel != TriggerLevel.NEVER
        val notificationsButtonState = when (state.selectedPlace) {
            Place.Current -> ToggleButtonState.Enabled(notificationChecked)
            is Place.Recent -> ToggleButtonState.Gone
            is Place.Favorite -> ToggleButtonState.Enabled(notificationChecked)
        }
        val kpIndexItem = state.selectedAuroraReport.kpIndex
            .toFactorItem(
                texts = ItemTexts.kpIndex,
                evaluate = kpIndexChanceEvaluator::evaluate,
                format = kpIndexFormatter::format,
            )
        val geomagLocationItem = state.selectedAuroraReport.geomagLocation
            .toFactorItem(
                texts = ItemTexts.geomagLocation,
                evaluate = geomagLocationChanceEvaluator::evaluate,
                format = geomagLocationFormatter::format,
            )
        val darknessItem = state.selectedAuroraReport.darkness
            .toFactorItem(
                texts = ItemTexts.darkness,
                evaluate = darknessChanceEvaluator::evaluate,
                format = darknessFormatter::format,
            )
        val weatherItem = state.selectedAuroraReport.weather
            .toFactorItem(
                texts = ItemTexts.weather,
                evaluate = weatherChanceEvaluator::evaluate,
                format = weatherFormatter::format,
            )
        val search = if (state.search is Search.Open) {
            val query = state.search.query
            val placesResults = state.places
                .map { place ->
                    val selected = place.id == state.selectedPlace.id
                    SearchResult.Known(place, selected = selected)
                }
                .filter { result ->
                    if (query.isBlank()) {
                        true
                    } else {
                        result.place.nameString.orEmpty().contains(query, ignoreCase = true)
                    }
                }
                .sortedByDescending { it.place.lastChanged ?: Instant.EPOCH }
                .sortedBy { it.place.priority }
            val searchResults = state.search.suggestions.items
                .map { suggestion ->
                    suggestion.toSearchResult()
                }
            val results = placesResults + searchResults
            SearchViewState.Open(query, results)
        } else SearchViewState.Closed
        val onFavoritesClickedEvent = when (val selectedPlace = selectedPlace) {
            Place.Current -> Event.Noop
            is Place.Recent -> Event.AddFavorite(selectedPlace)
            is Place.Favorite -> Event.RemoveFavorite(selectedPlace)
        }
        val notificationLevelItems = TriggerLevel.values()
            .map { level ->
                NotificationLevelItem(
                    text = level.shortText,
                    selected = level == selectedPlaceTriggerLevel,
                    selectEvent = Event.SetNotificationLevel(selectedPlace, level),
                )
            }
            .asReversed()
        return ViewState(
            toolbarTitleName = state.selectedPlace.name,
            chanceLevelText = changeLevelText,
            chanceSubtitleText = chanceSubtitleText,
            errorBannerData = errorBannerData,
            notificationsButtonState = notificationsButtonState,
            favoriteButtonState = favoriteButtonState,
            factorItems = listOf(kpIndexItem, geomagLocationItem, darknessItem, weatherItem),
            search = search,
            onFavoritesClickedEvent = onFavoritesClickedEvent,
            notificationLevelItems = notificationLevelItems,
        )
    }

    private fun <T : Any> Loadable<Report<T>>.toFactorItem(
        texts: ItemTexts,
        evaluate: (T) -> Chance,
        format: (T) -> TextRef
    ): FactorItem =
        when (this) {
            Loadable.Loading -> FactorItem.loading(texts)
            is Loadable.Loaded -> {
                when (val report = value) {
                    is Report.Success -> {
                        val valueText = format(report.value)
                        val chance = evaluate(report.value).value
                        FactorItem(
                            title = TextRef.stringRes(texts.shortTitle),
                            valueText = valueText,
                            descriptionText = TextRef.stringRes(texts.description),
                            valueTextColor = { onSurface },
                            progress = chance,
                            errorText = null,
                        )
                    }
                    is Report.Error -> FactorItem(
                        title = TextRef.stringRes(texts.shortTitle),
                        valueText = TextRef.string("?"),
                        descriptionText = TextRef.stringRes(texts.description),
                        valueTextColor = { error },
                        progress = null,
                        errorText = format(report.cause),
                    )
                    else -> error("Invalid report: $report")
                }
            }
        }

    private val Place.priority: Int
        get() = when (this) {
            Place.Current -> 0
            is Place.Favorite -> 1
            is Place.Recent -> 2
        }

    fun onEvent(event: Event) {
        @Suppress("UNUSED_VARIABLE")
        val dummy = scope.launch {
            when (event) {
                is Event.AddFavorite -> placesRepository.setFavorite(event.place.id)
                is Event.RemoveFavorite -> placesRepository.setRecent(event.place.id)
                is Event.SetNotificationLevel -> settings.setNotificationTriggerLevel(event.place, event.level)
                is Event.SearchChanged -> onSearchChanged(event.state)
                is Event.SelectSearchResult -> onSearchResultClicked(event.result)
                Event.RefreshLocationPermission -> permissionChecker.refresh()
                Event.Noop -> Unit
            }
        }
    }

    private fun onSearchChanged(searchFieldState: SearchFieldState) = store.issue { state ->
        state.update {
            val search = when (searchFieldState) {
                SearchFieldState.Inactive -> Search.Closed
                is SearchFieldState.Active -> {
                    val query = searchFieldState.text
                    when (search) {
                        Search.Closed -> Search.Open(
                            query = query,
                            suggestions = Suggestions("", emptyList()),
                            error = null,
                        )
                        is Search.Open -> search.copy(query = query)
                    }
                }
            }
            copy(search = search)
        }
    }

    private fun onSearchResultClicked(result: SearchResult) {
        scope.launch {
            val place = when (result) {
                is SearchResult.Known -> {
                    when (result.place) {
                        Place.Current -> result.place
                        is Place.Favorite, is Place.Recent -> placesRepository.updateLastChanged(result.place.id)
                    }
                }
                is SearchResult.New -> {
                    placesRepository.addRecent(result.name, result.location)
                }
            }
            selectedPlaceRepository.set(place)
            store.issue { state ->
                state.update { copy(search = Search.Closed) }
            }
        }
    }

    fun onBackPressed(): Boolean {
        return when (store.state.value.search) {
            Search.Closed -> false
            is Search.Open -> {
                store.issue { state ->
                    state.update { copy(search = Search.Closed) }
                }
                true
            }
        }
    }
}

private fun PlaceSuggestion.toSearchResult(): SearchResult.New {
    return SearchResult.New(simpleName, this.toDetailsString(), location)
}

private fun PlaceSuggestion.toDetailsString(): String {
    return fullName
        .removePrefix(simpleName)
        .dropWhile { !it.isLetterOrDigit() }
}

private fun format(cause: Cause): TextRef {
    val id = when (cause) {
        Cause.LocationPermission -> R.string.cause_location_permission
        Cause.Location -> R.string.cause_location
        Cause.Connectivity -> R.string.cause_connectivity
        Cause.ServerResponse -> R.string.cause_server_response
        Cause.Unknown -> R.string.cause_unknown
    }
    return TextRef.stringRes(id)
}

internal data class ItemTexts(
    @StringRes val shortTitle: Int,
    @StringRes val longTitle: Int,
    @StringRes val description: Int,
) {
    companion object {
        val kpIndex = ItemTexts(
            shortTitle = R.string.factor_kp_index_title_compact,
            longTitle = R.string.factor_kp_index_title_full,
            description = R.string.factor_kp_index_desc,
        )
        val geomagLocation = ItemTexts(
            shortTitle = R.string.factor_geomag_location_title_compact,
            longTitle = R.string.factor_geomag_location_title_full,
            description = R.string.factor_geomag_location_desc,
        )
        val darkness = ItemTexts(
            shortTitle = R.string.factor_darkness_title_compact,
            longTitle = R.string.factor_darkness_title_full,
            description = R.string.factor_darkness_desc,
        )
        val weather = ItemTexts(
            shortTitle = R.string.factor_weather_title_compact,
            longTitle = R.string.factor_weather_title_full,
            description = R.string.factor_weather_desc,
        )
    }
}

private val TriggerLevel.shortText: TextRef
    get() = when (this) {
        TriggerLevel.NEVER -> TextRef.stringRes(R.string.notify_never)
        TriggerLevel.LOW -> TextRef.stringRes(R.string.notify_at_low)
        TriggerLevel.MEDIUM -> TextRef.stringRes(R.string.notify_at_medium)
        TriggerLevel.HIGH -> TextRef.stringRes(R.string.notify_at_high)
    }
