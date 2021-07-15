package se.gustavkarlsson.skylight.android.feature.main.viewmodel

import androidx.annotation.StringRes
import androidx.compose.material.icons.filled.Warning
import arrow.core.Either
import com.ioki.textref.TextRef
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.core.entities.*
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
import se.gustavkarlsson.skylight.android.lib.permissions.Access
import se.gustavkarlsson.skylight.android.lib.permissions.Permission
import se.gustavkarlsson.skylight.android.lib.places.Place
import se.gustavkarlsson.skylight.android.lib.places.PlaceId
import se.gustavkarlsson.skylight.android.lib.ui.compose.Icons
import se.gustavkarlsson.skylight.android.lib.ui.compose.ToggleButtonState
import se.gustavkarlsson.skylight.android.lib.weather.Weather
import java.util.*
import javax.inject.Inject

internal class StateToViewStateMapper @Inject constructor(
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
) {

    fun map(state: State): ViewState = createViewState(state)

    private fun createViewState(state: State): ViewState {
        return when (state) {
            is State.Loading -> ViewState.Loading
            is State.Ready -> createNonLoadingState(state)
        }
    }

    private fun createNonLoadingState(state: State.Ready): ViewState {
        val triggerLevel = state.notificationTriggerLevels[PlaceId.Current]
        val requiresBackgroundLocationPermission = triggerLevel != TriggerLevel.NEVER
        val hasBackgroundPermission = state.permissions[Permission.BackgroundLocation] == Access.Granted
        return if (requiresBackgroundLocationPermission && !hasBackgroundPermission) {
            ViewState.RequiresBackgroundLocationPermission
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
                        } else TextRef.stringRes(R.string.your_location)
                    }
                    is Place.Saved -> TextRef.string(selectedPlace.name)
                }
                AppBarState.PlaceSelected(title)
            }
            is Search.Active -> AppBarState.Searching(state.search.query)
        }
    }

    private fun createContent(state: State.Ready): ContentState {
        return when (val search = state.search) {
            is Search.Active.Blank -> {
                val searchResults = createPlacesSearchResults(state, filter = null)
                    .sortedWith(searchResultOrderComparator)
                ContentState.Searching.Ok(searchResults)
            }
            is Search.Active.Filled -> {
                val searchResults = createPlacesSearchResults(state, filter = search.query)
                    .plus(createGeocodedSearchResults(search))
                    .sortedWith(searchResultOrderComparator)
                ContentState.Searching.Ok(searchResults)
            }
            is Search.Active.Error -> ContentState.Searching.Error(search.text)
            Search.Inactive -> {
                val currentSelected = state.selectedPlace == Place.Current
                val locationAccess = state.permissions[Permission.Location]
                if (currentSelected && locationAccess == Access.Denied) {
                    ContentState.RequiresLocationPermission.UseDialog
                } else if (currentSelected && locationAccess == Access.DeniedForever) {
                    ContentState.RequiresLocationPermission.UseAppSettings
                } else {
                    createSelectedPlaceContent(state)
                }
            }
        }
    }

    private fun createSelectedPlaceContent(state: State.Ready): ContentState.PlaceSelected {
        return ContentState.PlaceSelected(
            chanceLevelText = createChangeLevelText(state),
            errorBannerData = createErrorBannerData(state),
            notificationsButtonState = createNotificationButtonState(state),
            bookmarkButtonState = createBookmarkButtonState(state),
            factorItems = createFactorItems(state),
            onBookmarkClickedEvent = createOnBookmarkClickedEvent(state),
            notificationLevelItems = createNotificationLevelItems(state),
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
        val needsBackgroundLocation = state.notificationTriggerLevels.asMap().any { (placeId, triggerLevel) ->
            placeId == PlaceId.Current && triggerLevel != TriggerLevel.NEVER
        }
        val backgroundLocationDeniedSomehow = when (state.permissions[Permission.BackgroundLocation]) {
            Access.Denied, Access.DeniedForever -> true
            Access.Granted -> false
        }
        return when {
            needsBackgroundLocation && backgroundLocationDeniedSomehow -> {
                BannerData(
                    TextRef.stringRes(R.string.background_location_permission_denied_message),
                    TextRef.stringRes(R.string.open_settings),
                    Icons.Warning,
                    BannerData.Event.OpenAppDetails
                )
            }
            state.selectedPlace != Place.Current -> null
            else -> null
        }
    }

    private fun createNotificationButtonState(state: State.Ready): ToggleButtonState {
        val notificationChecked = state.selectedPlaceTriggerLevel != TriggerLevel.NEVER
        return ToggleButtonState.Enabled(notificationChecked)
    }

    private fun createBookmarkButtonState(state: State.Ready): ToggleButtonState {
        return when (val selectedPlace = state.selectedPlace) {
            Place.Current -> ToggleButtonState.Gone
            is Place.Saved -> ToggleButtonState.Enabled(selectedPlace.bookmarked)
        }
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
            .reportToFactorItem(
                texts = ItemTexts.WEATHER,
                evaluator = weatherChanceEvaluator,
                formatter = weatherFormatter,
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
                } else true
            }
            .map { place ->
                val selected = place.id == state.selectedPlace.id
                val notifications = state.notificationTriggerLevels[place.id] != TriggerLevel.NEVER
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
            .mapNotNull { result ->
                result.orNull()
            }
            .orNull()
    }

    private fun createOnBookmarkClickedEvent(state: State.Ready): Event {
        return when (val selectedPlace = state.selectedPlace) {
            Place.Current -> Event.Noop
            is Place.Saved -> if (selectedPlace.bookmarked) {
                Event.RemoveBookmark(selectedPlace)
            } else Event.AddBookmark(selectedPlace)
        }
    }

    private fun createNotificationLevelItems(state: State.Ready): List<NotificationLevelItem> {
        return TriggerLevel.values()
            .sortedBy { level -> level.displayIndex }
            .map { level ->
                NotificationLevelItem(
                    text = level.shortText,
                    selected = level == state.selectedPlaceTriggerLevel,
                    selectEvent = Event.SetNotificationLevel(state.selectedPlace, level),
                )
            }
    }

    private fun <T : Any> Loadable<Report<T>>.reportToFactorItem(
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
        ifSome = { report ->
            when (report) {
                is Report.Success -> {
                    val value = report.value
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
    )

    private fun <L, R> Loadable<Either<L, R>>.resultToFactorItem(
        texts: ItemTexts,
        evaluator: ChanceEvaluator<R>,
        formatter: Formatter<R>,
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
                        errorText = TextRef.EMPTY, // FIXME get error message
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
                }
            )
        }
    )

    // FIXME avoid duplication with similar functions above
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
        }
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
        is SearchResult.Known.Saved -> if (place.bookmarked) {
            2
        } else 3
        is SearchResult.New -> 4
    }

private val SearchResult.lastChanged: Instant
    get() = when (this) {
        is SearchResult.Known.Saved -> place.lastChanged
        is SearchResult.Known.Current, is SearchResult.New -> Instant.EPOCH
    }

private fun PlaceSuggestion.toDetailsString(): String {
    return fullName
        .removePrefix(simpleName)
        .dropWhile { !it.isLetterOrDigit() }
}

private fun format(cause: Cause): TextRef {
    val id = when (cause) {
        Cause.NoLocationPermission -> R.string.cause_location_permission
        Cause.NoLocation -> R.string.cause_location
        Cause.Connectivity -> R.string.cause_connectivity
        Cause.ServerResponse -> R.string.cause_server_response
        Cause.Unknown -> R.string.cause_unknown
    }
    return TextRef.stringRes(id)
}

private val TriggerLevel.displayIndex: Int
    get() = when (this) {
        TriggerLevel.NEVER -> 1
        TriggerLevel.HIGH -> 2
        TriggerLevel.MEDIUM -> 3
        TriggerLevel.LOW -> 4
    }

private val TriggerLevel.shortText: TextRef
    get() = when (this) {
        TriggerLevel.NEVER -> TextRef.stringRes(R.string.notify_never)
        TriggerLevel.LOW -> TextRef.stringRes(R.string.notify_at_low)
        TriggerLevel.MEDIUM -> TextRef.stringRes(R.string.notify_at_medium)
        TriggerLevel.HIGH -> TextRef.stringRes(R.string.notify_at_high)
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
