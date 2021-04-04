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
                relativeTimeFormatter.format(timestamp, time.now(), nowTextThreshold)
            }
            when {
                relativeTime == null -> TextRef.EMPTY
                name == null -> relativeTime
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
            is Place.Saved.Recent -> ToggleButtonState.Enabled(checked = false)
            is Place.Saved.Favorite -> ToggleButtonState.Enabled(checked = true)
        }
        val notificationChecked = state.selectedPlaceTriggerLevel != TriggerLevel.NEVER
        val notificationsButtonState = when (state.selectedPlace) {
            Place.Current -> ToggleButtonState.Enabled(notificationChecked)
            is Place.Saved.Recent -> ToggleButtonState.Gone
            is Place.Saved.Favorite -> ToggleButtonState.Enabled(notificationChecked)
        }
        val kpIndexItem = state.selectedAuroraReport.kpIndex
            .toFactorItem(
                texts = ItemTexts.KP_INDEX,
                evaluate = kpIndexChanceEvaluator::evaluate,
                format = kpIndexFormatter::format,
            )
        val geomagLocationItem = state.selectedAuroraReport.geomagLocation
            .toFactorItem(
                texts = ItemTexts.GEOMAG_LOCATION,
                evaluate = geomagLocationChanceEvaluator::evaluate,
                format = geomagLocationFormatter::format,
            )
        val darknessItem = state.selectedAuroraReport.darkness
            .toFactorItem(
                texts = ItemTexts.DARKNESS,
                evaluate = darknessChanceEvaluator::evaluate,
                format = darknessFormatter::format,
            )
        val weatherItem = state.selectedAuroraReport.weather
            .toFactorItem(
                texts = ItemTexts.WEATHER,
                evaluate = weatherChanceEvaluator::evaluate,
                format = weatherFormatter::format,
            )
        // FIXME rework how this is created
        val search = if (state.search is Search.Active) {
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
                        when (result.place) {
                            Place.Current -> false
                            is Place.Saved -> result.place.name.contains(query, ignoreCase = true)
                        }
                    }
                }
                .sortedByDescending {
                    when (it.place) {
                        Place.Current -> Instant.EPOCH
                        is Place.Saved -> it.place.lastChanged
                    }
                }
                .sortedBy { it.place.priority }
            val searchResults = state.search.suggestions.items
                .map { suggestion ->
                    suggestion.toSearchResult()
                }
            val results = placesResults + searchResults
            when (state.search) {
                is Search.Active.Blank, is Search.Active.Filled -> {
                    SearchViewState.Open.Ok(query, results)
                }
                is Search.Active.Error -> {
                    SearchViewState.Open.Error(query, state.search.text)
                }
            }
        } else SearchViewState.Closed
        val onFavoritesClickedEvent = when (val selectedPlace = state.selectedPlace) {
            Place.Current -> Event.Noop
            is Place.Saved.Recent -> Event.AddFavorite(selectedPlace)
            is Place.Saved.Favorite -> Event.RemoveFavorite(selectedPlace)
        }
        val notificationLevelItems = TriggerLevel.values()
            .map { level ->
                NotificationLevelItem(
                    text = level.shortText,
                    selected = level == state.selectedPlaceTriggerLevel,
                    selectEvent = Event.SetNotificationLevel(state.selectedPlace, level),
                )
            }
            .asReversed()
        return ViewState(
            toolbarTitleName = state.selectedPlace.displayName,
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
