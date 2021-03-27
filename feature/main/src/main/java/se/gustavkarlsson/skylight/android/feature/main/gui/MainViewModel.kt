package se.gustavkarlsson.skylight.android.feature.main.gui

import androidx.annotation.StringRes
import androidx.compose.material.Colors
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.ioki.textref.TextRef
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.threeten.bp.Duration
import se.gustavkarlsson.conveyor.Store
import se.gustavkarlsson.conveyor.issue
import se.gustavkarlsson.koptional.optionalOf
import se.gustavkarlsson.skylight.android.core.entities.Cause
import se.gustavkarlsson.skylight.android.core.entities.Chance
import se.gustavkarlsson.skylight.android.core.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.entities.Report
import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.core.services.Formatter
import se.gustavkarlsson.skylight.android.feature.main.R
import se.gustavkarlsson.skylight.android.feature.main.RelativeTimeFormatter
import se.gustavkarlsson.skylight.android.feature.main.State
import se.gustavkarlsson.skylight.android.lib.aurora.CompleteAuroraReport
import se.gustavkarlsson.skylight.android.lib.darkness.Darkness
import se.gustavkarlsson.skylight.android.lib.geomaglocation.GeomagLocation
import se.gustavkarlsson.skylight.android.lib.kpindex.KpIndex
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.permissions.Access
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionChecker
import se.gustavkarlsson.skylight.android.lib.places.Place
import se.gustavkarlsson.skylight.android.lib.places.PlacesRepository
import se.gustavkarlsson.skylight.android.lib.places.SelectedPlaceRepository
import se.gustavkarlsson.skylight.android.lib.reversegeocoder.ReverseGeocodingResult
import se.gustavkarlsson.skylight.android.lib.time.Time
import se.gustavkarlsson.skylight.android.lib.ui.CoroutineScopedService
import se.gustavkarlsson.skylight.android.lib.ui.compose.Icons
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
    private val nowTextThreshold: Duration
) : CoroutineScopedService() {

    init {
        store.start(scope)
    }

    val viewState: StateFlow<ViewState> = store.state
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
        val searchResults = if (state.searchFocused) {
            state.places.map { place -> SearchResult.Known(place) }
        } else null
        return ViewState(
            toolbarTitleName = state.selectedPlace.name,
            chanceLevelText = changeLevelText,
            chanceSubtitleText = chanceSubtitleText,
            errorBannerData = errorBannerData,
            factorItems = listOf(kpIndexItem, geomagLocationItem, darknessItem, weatherItem),
            searchText = state.searchText,
            searchResults = searchResults,
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

    fun refreshLocationPermission() = permissionChecker.refresh()

    fun onSearchTextChanged(text: String) = store.issue { state ->
        state.update { copy(searchText = text) }
    }

    fun onSearchFocusChanged(focused: Boolean) = store.issue { state ->
        state.update { copy(searchFocused = focused) }
    }

    fun onSearchResultClicked(result: SearchResult) {
        scope.launch {
            val place = when (result) {
                is SearchResult.Known -> result.place
                is SearchResult.New -> {
                    placesRepository.addRecent(result.name, result.location)
                }
            }
            if (place is Place.Recent) {
                placesRepository.setLastChanged(place.id, time.now())
            }
            selectedPlaceRepository.set(place)
        }
    }
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

internal data class FactorItem(
    val title: TextRef,
    val valueText: TextRef,
    val descriptionText: TextRef,
    val valueTextColor: Colors.() -> Color,
    val progress: Double?,
    val errorText: TextRef?
) {
    companion object {
        fun loading(
            texts: ItemTexts,
        ): FactorItem {
            return FactorItem(
                title = TextRef.stringRes(texts.shortTitle),
                valueText = TextRef.string("…"),
                descriptionText = TextRef.stringRes(texts.description),
                valueTextColor = { onSurface.copy(alpha = 0.7F) },
                progress = null,
                errorText = null,
            )
        }
    }
}

internal data class BannerData(
    val message: TextRef,
    val buttonText: TextRef,
    val icon: ImageVector,
    val buttonEvent: Event
) {
    enum class Event {
        RequestLocationPermission, OpenAppDetails
    }
}

internal data class ViewState(
    val toolbarTitleName: TextRef,
    val chanceLevelText: TextRef,
    val chanceSubtitleText: TextRef,
    val errorBannerData: BannerData?,
    val factorItems: List<FactorItem>,
    val searchText: String,
    val searchResults: List<SearchResult>?,
)

internal sealed class SearchResult {
    abstract val title: TextRef
    open val subtitle: TextRef? = null
    abstract val icon: ImageVector

    data class Known(val place: Place) : SearchResult() {
        override val title: TextRef get() = place.name
        override val icon: ImageVector = when (place) {
            Place.Current -> Icons.MyLocation
            is Place.Favorite -> Icons.Star
            is Place.Recent -> Icons.History
        }
    }

    data class New(
        val name: String,
        val caption: String,
        val location: Location,
    ) : SearchResult() {
        override val title: TextRef = TextRef.string(name)
        override val subtitle: TextRef = TextRef.string(caption)
        override val icon: ImageVector = Icons.Map
    }
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
