package se.gustavkarlsson.skylight.android.feature.main.viewmodel

import androidx.annotation.StringRes
import androidx.compose.material.icons.filled.Warning
import arrow.core.Either
import com.ioki.textref.TextRef
import kotlinx.datetime.Instant
import me.tatarka.inject.annotations.Inject
import se.gustavkarlsson.skylight.android.core.entities.Chance
import se.gustavkarlsson.skylight.android.core.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.core.services.Formatter
import se.gustavkarlsson.skylight.android.feature.main.R
import se.gustavkarlsson.skylight.android.feature.main.state.Search
import se.gustavkarlsson.skylight.android.feature.main.state.State
import se.gustavkarlsson.skylight.android.lib.aurora.CompleteAuroraReport
import se.gustavkarlsson.skylight.android.lib.darkness.Darkness
import se.gustavkarlsson.skylight.android.lib.geocoder.PlaceSuggestion
import se.gustavkarlsson.skylight.android.lib.geomaglocation.GeomagLocation
import se.gustavkarlsson.skylight.android.lib.kpindex.KpIndex
import se.gustavkarlsson.skylight.android.lib.kpindex.KpIndexError
import se.gustavkarlsson.skylight.android.lib.location.LocationServiceStatus
import se.gustavkarlsson.skylight.android.lib.permissions.Access
import se.gustavkarlsson.skylight.android.lib.permissions.Permission
import se.gustavkarlsson.skylight.android.lib.places.Place
import se.gustavkarlsson.skylight.android.lib.places.PlaceId
import se.gustavkarlsson.skylight.android.lib.ui.compose.Icons
import se.gustavkarlsson.skylight.android.lib.ui.compose.ToggleButtonState
import se.gustavkarlsson.skylight.android.lib.weather.Weather
import se.gustavkarlsson.skylight.android.lib.weather.WeatherError
import se.gustavkarlsson.skylight.android.core.R as CoreR

@Inject
internal class StateToViewStateMapper(
    private val auroraChanceEvaluator: ChanceEvaluator<CompleteAuroraReport>,
    private val chanceLevelFormatter: Formatter<ChanceLevel>,
    private val darknessChanceEvaluator: ChanceEvaluator<Darkness>,
    private val darknessFormatter: Formatter<Darkness>,
    private val geomagLocationChanceEvaluator: ChanceEvaluator<GeomagLocation>,
    private val geomagLocationFormatter: Formatter<GeomagLocation>,
    private val kpIndexChanceEvaluator: ChanceEvaluator<KpIndex>,
    private val kpIndexFormatter: Formatter<KpIndex>,
    private val weatherChanceEvaluator: ChanceEvaluator<Weather>,
    private val weatherFormatter: Formatter<Weather>,
    @BackgroundLocationName private val backgroundLocationName: String,
) {

    fun map(state: State): ViewState = createViewState(state)

    private fun createViewState(state: State): ViewState {
        return when (state) {
            is State.Loading -> ViewState.Loading
            is State.Ready -> createNonLoadingState(state)
        }
    }

    private fun createNonLoadingState(state: State.Ready): ViewState {
        val requiresBackgroundLocationPermission = PlaceId.Current in state.settings.placeIdsWithNotification
        val hasLocationPermission = state.permissions[Permission.Location] == Access.Granted
        val hasBackgroundPermission = state.permissions[Permission.BackgroundLocation] == Access.Granted
        return if (hasLocationPermission && requiresBackgroundLocationPermission && !hasBackgroundPermission) {
            if (state.permissions[Permission.BackgroundLocation] == Access.DeniedForever) {
                val description = TextRef.stringRes(
                    R.string.background_location_permission_denied_forever_message,
                    backgroundLocationName,
                )
                ViewState.RequiresBackgroundLocationPermission.UseAppSettings(description)
            } else {
                val description = TextRef.stringRes(
                    R.string.background_location_permission_required_message,
                    backgroundLocationName,
                )
                ViewState.RequiresBackgroundLocationPermission.UseDialog(description)
            }
        } else {
            ViewState.Ready(
                appBar = createToolbarState(state),
                content = createContent(state),
            )
        }
    }

    private fun createToolbarState(state: State.Ready): AppBarState {
        return when (state.search) {
            is Search.Inactive -> {
                val title = when (val selectedPlace = state.selectedPlace) {
                    Place.Current -> {
                        val name = createCurrentLocationDisplayName(state)
                        if (name != null) {
                            TextRef.string(name)
                        } else {
                            TextRef.stringRes(CoreR.string.your_location)
                        }
                    }
                    is Place.Saved -> TextRef.string(selectedPlace.name)
                }
                AppBarState.PlaceSelected(title)
            }
            is Search.Active -> AppBarState.Searching(state.search.query)
        }
    }

    private fun createContent(state: State.Ready): ContentState {
        val deletePlaceDialog = state.placeToDelete?.let(::createDeletePlaceDialog)
        return when (val search = state.search) {
            is Search.Active.Blank -> {
                val searchResults = createPlacesSearchResults(state, filter = null)
                    .sortedWith(searchResultOrderComparator)
                ContentState.Searching.Ok(
                    searchResults = searchResults,
                    dialog = deletePlaceDialog,
                )
            }
            is Search.Active.Filled -> {
                val searchResults = createPlacesSearchResults(state, filter = search.query.trim())
                    .plus(createGeocodedSearchResults(search))
                    .mergeDuplicates()
                    .sortedWith(searchResultOrderComparator)
                ContentState.Searching.Ok(
                    searchResults = searchResults,
                    dialog = deletePlaceDialog,
                )
            }
            is Search.Active.Error -> ContentState.Searching.Error(search.text)
            Search.Inactive -> {
                val currentSelected = state.selectedPlace == Place.Current
                val locationServiceEnabled = state.locationServiceStatus == LocationServiceStatus.Enabled
                val locationAccess = state.permissions[Permission.Location]
                when {
                    currentSelected && !locationServiceEnabled -> {
                        ContentState.RequiresLocationService
                    }
                    currentSelected && locationAccess == Access.Denied -> {
                        ContentState.RequiresLocationPermission.UseDialog
                    }
                    currentSelected && locationAccess == Access.DeniedForever -> {
                        ContentState.RequiresLocationPermission.UseAppSettings
                    }
                    else -> {
                        createSelectedPlaceContent(state)
                    }
                }
            }
        }
    }

    private fun createDeletePlaceDialog(placeToDelete: Place.Saved): DialogData {
        return DialogData(
            text = TextRef.stringRes(R.string.delete_place_question, placeToDelete.name),
            dismissEvent = Event.CancelPlaceDeletion,
            confirmData = ButtonData(TextRef.stringRes(CoreR.string.delete), Event.DeletePlace(placeToDelete)),
            cancelData = ButtonData(TextRef.stringRes(CoreR.string.cancel), Event.CancelPlaceDeletion),
        )
    }

    private fun createSelectedPlaceContent(state: State.Ready): ContentState.PlaceSelected {
        return ContentState.PlaceSelected(
            chanceLevelText = createChangeLevelText(state),
            errorBannerData = createErrorBannerData(state),
            notificationsButtonState = createNotificationButtonState(state),
            factorItems = createFactorItems(state),
            onNotificationClickedEvent = createOnNotificationClickedEvent(state),
        )
    }

    private fun createChangeLevelText(state: State): TextRef {
        val chance = state.selectedAuroraReport.toCompleteAuroraReport()
            ?.let(auroraChanceEvaluator::evaluate)
            ?: Chance.UNKNOWN
        val level = ChanceLevel.fromChance(chance)
        return chanceLevelFormatter.format(level)
    }

    private fun createErrorBannerData(state: State.Ready): BannerData? {
        val needsBackgroundLocation = PlaceId.Current in state.settings.placeIdsWithNotification
        val backgroundLocationDeniedSomehow = when (state.permissions[Permission.BackgroundLocation]) {
            Access.Denied, Access.DeniedForever -> true
            Access.Granted -> false
        }
        return when {
            needsBackgroundLocation && backgroundLocationDeniedSomehow -> {
                BannerData(
                    TextRef.stringRes(R.string.banner_location_permission_issue),
                    TextRef.stringRes(R.string.fix),
                    Icons.Warning,
                    Event.SelectPlace(PlaceId.Current),
                )
            }
            state.selectedPlace != Place.Current -> null
            else -> null
        }
    }

    private fun createNotificationButtonState(state: State.Ready): ToggleButtonState {
        val notificationChecked = state.selectedPlace.id in state.settings.placeIdsWithNotification
        return ToggleButtonState.Enabled(notificationChecked)
    }

    private fun createFactorItems(state: State): List<FactorItem> {
        return listOf(
            createKpIndexItem(state),
            createGeomagLocationItem(state),
            createDarknessItem(state),
            createWeatherItem(state),
        )
    }

    private fun createKpIndexItem(state: State): FactorItem {
        return state.selectedAuroraReport.kpIndex
            .resultToFactorItem(
                texts = ItemTexts.KP_INDEX,
                evaluator = kpIndexChanceEvaluator,
                formatter = kpIndexFormatter,
                errorFormatter = { error ->
                    val text = when (error) {
                        KpIndexError.Connectivity -> R.string.cause_connectivity
                        KpIndexError.ServerResponse -> R.string.cause_server_response
                        KpIndexError.Unknown -> R.string.cause_unknown
                    }
                    TextRef.stringRes(text)
                },
            )
    }

    private fun createGeomagLocationItem(state: State): FactorItem {
        return state.selectedAuroraReport.geomagLocation
            .toFactorItem(
                texts = ItemTexts.GEOMAG_LOCATION,
                evaluator = geomagLocationChanceEvaluator,
                formatter = geomagLocationFormatter,
            )
    }

    private fun createDarknessItem(state: State): FactorItem {
        return state.selectedAuroraReport.darkness
            .toFactorItem(
                texts = ItemTexts.DARKNESS,
                evaluator = darknessChanceEvaluator,
                formatter = darknessFormatter,
            )
    }

    private fun createWeatherItem(state: State): FactorItem {
        return state.selectedAuroraReport.weather
            .resultToFactorItem(
                texts = ItemTexts.WEATHER,
                evaluator = weatherChanceEvaluator,
                formatter = weatherFormatter,
                errorFormatter = { error ->
                    val text = when (error) {
                        WeatherError.Connectivity -> R.string.cause_connectivity
                        WeatherError.ServerResponse -> R.string.cause_server_response
                        WeatherError.Unknown -> R.string.cause_unknown
                    }
                    TextRef.stringRes(text)
                },
            )
    }

    private fun createPlacesSearchResults(state: State.Ready, filter: String?): List<SearchResult.Known> {
        return state.places
            .filter { place ->
                if (filter != null) {
                    when (place) {
                        Place.Current -> false
                        is Place.Saved -> place.name.contains(filter, ignoreCase = true)
                    }
                } else {
                    true
                }
            }
            .map { place ->
                val selected = place.id == state.selectedPlace.id
                val notifications = place.id in state.settings.placeIdsWithNotification
                when (place) {
                    Place.Current -> {
                        val name = createCurrentLocationDisplayName(state)
                        SearchResult.Known.Current(name, selected, notifications)
                    }
                    is Place.Saved -> SearchResult.Known.Saved(place, selected, notifications)
                }
            }
    }

    private fun createGeocodedSearchResults(search: Search.Active.Filled): List<SearchResult.New> {
        return search.suggestions.map { suggestion ->
            SearchResult.New(suggestion.simpleName, suggestion.toDetailsString(), suggestion.location)
        }
    }

    private fun createCurrentLocationDisplayName(state: State): String? {
        return state.currentLocationName
            .flatMap { result ->
                result.getOrNone()
            }
            .getOrNull()
    }

    private fun createOnNotificationClickedEvent(state: State.Ready): Event {
        val selectedPlace = state.selectedPlace
        val enabled = selectedPlace.id in state.settings.placeIdsWithNotification
        return Event.SetNotifications(selectedPlace, !enabled)
    }

    // TODO avoid duplication with similar function below
    private fun <L, R> Loadable<Either<L, R>>.resultToFactorItem(
        texts: ItemTexts,
        evaluator: ChanceEvaluator<R>,
        formatter: Formatter<R>,
        errorFormatter: Formatter<L>,
    ): FactorItem = fold(
        ifEmpty = {
            FactorItem(
                title = TextRef.stringRes(texts.shortTitle),
                valueText = TextRef.string("…"),
                descriptionText = TextRef.stringRes(texts.description),
                valueTextColor = { onSurface.copy(alpha = 0.7F) },
                progress = null,
                errorText = null,
            )
        },
        ifSome = { result ->
            result.fold(
                ifLeft = { error ->
                    FactorItem(
                        title = TextRef.stringRes(texts.shortTitle),
                        valueText = TextRef.string("?"),
                        descriptionText = TextRef.stringRes(texts.description),
                        valueTextColor = { this.error },
                        progress = null,
                        errorText = errorFormatter.format(error),
                    )
                },
                ifRight = { value ->
                    val valueText = formatter.format(value)
                    val chance = evaluator.evaluate(value).value
                    FactorItem(
                        title = TextRef.stringRes(texts.shortTitle),
                        valueText = valueText,
                        descriptionText = TextRef.stringRes(texts.description),
                        valueTextColor = { onSurface },
                        progress = chance,
                        errorText = null,
                    )
                },
            )
        },
    )

    // TODO avoid duplication with similar function above
    private fun <T : Any> Loadable<T>.toFactorItem(
        texts: ItemTexts,
        evaluator: ChanceEvaluator<T>,
        formatter: Formatter<T>,
    ): FactorItem = fold(
        ifEmpty = {
            FactorItem(
                title = TextRef.stringRes(texts.shortTitle),
                valueText = TextRef.string("…"),
                descriptionText = TextRef.stringRes(texts.description),
                valueTextColor = { onSurface.copy(alpha = 0.7F) },
                progress = null,
                errorText = null,
            )
        },
        ifSome = { value ->
            val valueText = formatter.format(value)
            val chance = evaluator.evaluate(value).value
            FactorItem(
                title = TextRef.stringRes(texts.shortTitle),
                valueText = valueText,
                descriptionText = TextRef.stringRes(texts.description),
                valueTextColor = { onSurface },
                progress = chance,
                errorText = null,
            )
        },
    )
}

private val searchResultOrderComparator: Comparator<SearchResult>
    get() {
        return compareBy<SearchResult> { result ->
            result.typePriority
        }.thenByDescending { result ->
            result.lastChanged
        }
    }

private val SearchResult.typePriority: Int
    get() = when (this) {
        is SearchResult.Known.Current -> 1
        is SearchResult.Known.Saved -> 2
        is SearchResult.New -> 3
    }

private val SearchResult.lastChanged: Instant
    get() = when (this) {
        is SearchResult.Known.Saved -> place.lastChanged
        is SearchResult.Known.Current, is SearchResult.New -> Instant.fromEpochMilliseconds(0)
    }

private fun List<SearchResult>.mergeDuplicates(): List<SearchResult> {
    return groupBy { result ->
        when (result) {
            is SearchResult.Known.Current -> null
            is SearchResult.Known.Saved -> result.place.location
            is SearchResult.New -> result.location
        }
    }.mapValues { (_, results) ->
        results.firstOrNull { it is SearchResult.Known } ?: results.first()
    }.values.toList()
}

private fun PlaceSuggestion.toDetailsString(): String {
    return fullName
        .removePrefix(simpleName)
        .dropWhile { !it.isLetterOrDigit() }
}

private enum class ItemTexts(
    @StringRes val shortTitle: Int,
    @StringRes val longTitle: Int,
    @StringRes val description: Int,
) {
    KP_INDEX(
        shortTitle = R.string.factor_kp_index_title_compact,
        longTitle = R.string.factor_kp_index_title_full,
        description = R.string.factor_kp_index_desc,
    ),
    GEOMAG_LOCATION(
        shortTitle = R.string.factor_geomag_location_title_compact,
        longTitle = R.string.factor_geomag_location_title_full,
        description = R.string.factor_geomag_location_desc,
    ),
    DARKNESS(
        shortTitle = R.string.factor_darkness_title_compact,
        longTitle = R.string.factor_darkness_title_full,
        description = R.string.factor_darkness_desc,
    ),
    WEATHER(
        shortTitle = R.string.factor_weather_title_compact,
        longTitle = R.string.factor_weather_title_full,
        description = R.string.factor_weather_desc,
    ),
}
