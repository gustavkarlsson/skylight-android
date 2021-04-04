package se.gustavkarlsson.skylight.android.feature.main.viewmodel

import androidx.annotation.StringRes
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import com.ioki.textref.TextRef
import javax.inject.Inject
import org.threeten.bp.Duration
import org.threeten.bp.Instant
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
import se.gustavkarlsson.skylight.android.feature.main.util.RelativeTimeFormatter
import se.gustavkarlsson.skylight.android.lib.aurora.CompleteAuroraReport
import se.gustavkarlsson.skylight.android.lib.darkness.Darkness
import se.gustavkarlsson.skylight.android.lib.geocoder.PlaceSuggestion
import se.gustavkarlsson.skylight.android.lib.geomaglocation.GeomagLocation
import se.gustavkarlsson.skylight.android.lib.kpindex.KpIndex
import se.gustavkarlsson.skylight.android.lib.permissions.Access
import se.gustavkarlsson.skylight.android.lib.places.Place
import se.gustavkarlsson.skylight.android.lib.reversegeocoder.ReverseGeocodingResult
import se.gustavkarlsson.skylight.android.lib.time.Time
import se.gustavkarlsson.skylight.android.lib.ui.compose.Icons
import se.gustavkarlsson.skylight.android.lib.ui.compose.ToggleButtonState
import se.gustavkarlsson.skylight.android.lib.weather.Weather

internal class StateToViewStateMapper @Inject constructor(
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
    private val time: Time,
    @NowThreshold private val nowTextThreshold: Duration
) {

    fun map(state: State): ViewState {
        return ViewState(
            toolbarTitleName = state.selectedPlace.displayName,
            chanceLevelText = createChangeLevelText(state),
            chanceSubtitleText = createChanceSubtitleText(state),
            errorBannerData = createErrorBannerData(state),
            notificationsButtonState = createNotificationButtonState(state),
            favoriteButtonState = createFavoriteButtonState(state),
            factorItems = createFactorItems(state),
            search = createSearchViewState(state),
            onFavoritesClickedEvent = createOnFavoritesClickedEvent(state),
            notificationLevelItems = createNotificationLevelItems(state),
        )
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
            .toFactorItem(
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
            .toFactorItem(
                texts = ItemTexts.WEATHER,
                evaluator = weatherChanceEvaluator,
                formatter = weatherFormatter,
            )
    }

    private fun createChangeLevelText(state: State): TextRef {
        val chance = state.selectedAuroraReport.toCompleteAuroraReport()
            ?.let(auroraChanceEvaluator::evaluate)
            ?: Chance.UNKNOWN
        val level = ChanceLevel.fromChance(chance)
        return chanceLevelFormatter.format(level)
    }

    private fun createChanceSubtitleText(state: State): TextRef {
        val name = optionalOf(state.selectedAuroraReport.locationName)
            .map { it as? Loadable.Loaded<ReverseGeocodingResult> }
            .map { it.value as? ReverseGeocodingResult.Success }
            .map { it.name }
            .value
        val relativeTime = state.selectedAuroraReport.timestamp?.let { timestamp ->
            // FIXME emit new time every sec
            relativeTimeFormatter.format(timestamp, time.now(), nowTextThreshold)
        }
        return when {
            relativeTime == null -> TextRef.EMPTY
            name == null -> relativeTime
            else -> TextRef.stringRes(R.string.time_in_location, relativeTime, name)
        }
    }

    private fun createErrorBannerData(state: State): BannerData? {
        return when {
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
    }

    private fun createNotificationButtonState(state: State): ToggleButtonState {
        val notificationChecked = state.selectedPlaceTriggerLevel != TriggerLevel.NEVER
        return when (state.selectedPlace) {
            Place.Current -> ToggleButtonState.Enabled(notificationChecked)
            is Place.Saved.Recent -> ToggleButtonState.Gone
            is Place.Saved.Favorite -> ToggleButtonState.Enabled(notificationChecked)
        }
    }

    private fun createFavoriteButtonState(state: State): ToggleButtonState {
        return when (state.selectedPlace) {
            Place.Current -> ToggleButtonState.Gone
            is Place.Saved.Recent -> ToggleButtonState.Enabled(checked = false)
            is Place.Saved.Favorite -> ToggleButtonState.Enabled(checked = true)
        }
    }

    // FIXME rework how this is created
    private fun createSearchViewState(state: State): SearchViewState {
        return if (state.search is Search.Active) {
            val search = state.search
            val query = search.query
            val placesResults = state.places
                .map { place ->
                    val selected = place.id == state.selectedPlace.id
                    SearchResult.Known(place, selected = selected)
                }
                .filter { result ->
                    if (query.isBlank()) {
                        true
                    } else {
                        when (val place = result.place) {
                            Place.Current -> false
                            is Place.Saved -> place.name.contains(query, ignoreCase = true)
                        }
                    }
                }
                .sortedByDescending { result ->
                    when (val place = result.place) {
                        Place.Current -> Instant.EPOCH
                        is Place.Saved -> place.lastChanged
                    }
                }
                .sortedBy { result -> result.place.priority }
            val searchResults = search.suggestions.items
                .map { suggestion ->
                    suggestion.toSearchResult()
                }
            val results = placesResults + searchResults
            when (search) {
                is Search.Active.Blank, is Search.Active.Filled -> {
                    SearchViewState.Open.Ok(query, results)
                }
                is Search.Active.Error -> {
                    SearchViewState.Open.Error(query, search.text)
                }
            }
        } else SearchViewState.Closed
    }

    private fun createOnFavoritesClickedEvent(state: State): Event {
        return when (val selectedPlace = state.selectedPlace) {
            Place.Current -> Event.Noop
            is Place.Saved.Recent -> Event.AddFavorite(selectedPlace)
            is Place.Saved.Favorite -> Event.RemoveFavorite(selectedPlace)
        }
    }

    private fun createNotificationLevelItems(state: State): List<NotificationLevelItem> {
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

    private fun <T : Any> Loadable<Report<T>>.toFactorItem(
        texts: ItemTexts,
        evaluator: ChanceEvaluator<T>,
        formatter: Formatter<T>,
    ): FactorItem =
        when (this) {
            Loadable.Loading -> FactorItem.loading(texts)
            is Loadable.Loaded -> {
                when (val report = value) {
                    is Report.Success -> {
                        val valueText = formatter.format(report.value)
                        val chance = evaluator.evaluate(report.value).value
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
            is Place.Saved.Favorite -> 1
            is Place.Saved.Recent -> 2
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

internal enum class ItemTexts(
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
