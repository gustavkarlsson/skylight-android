package se.gustavkarlsson.skylight.android.feature.main.gui

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.ioki.textref.TextRef
import de.halfbit.knot.Knot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.rx2.asFlow
import org.threeten.bp.Duration
import se.gustavkarlsson.koptional.Absent
import se.gustavkarlsson.koptional.Optional
import se.gustavkarlsson.koptional.Present
import se.gustavkarlsson.koptional.toOptional
import se.gustavkarlsson.skylight.android.core.entities.Cause
import se.gustavkarlsson.skylight.android.core.entities.Chance
import se.gustavkarlsson.skylight.android.core.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.entities.Report
import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.core.services.Formatter
import se.gustavkarlsson.skylight.android.feature.main.ChanceToColorConverter
import se.gustavkarlsson.skylight.android.feature.main.Change
import se.gustavkarlsson.skylight.android.feature.main.R
import se.gustavkarlsson.skylight.android.feature.main.RelativeTimeFormatter
import se.gustavkarlsson.skylight.android.feature.main.State
import se.gustavkarlsson.skylight.android.lib.aurora.CompleteAuroraReport
import se.gustavkarlsson.skylight.android.lib.aurora.LoadableAuroraReport
import se.gustavkarlsson.skylight.android.lib.darkness.Darkness
import se.gustavkarlsson.skylight.android.lib.geomaglocation.GeomagLocation
import se.gustavkarlsson.skylight.android.lib.kpindex.KpIndex
import se.gustavkarlsson.skylight.android.lib.permissions.Access
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionChecker
import se.gustavkarlsson.skylight.android.lib.places.Place
import se.gustavkarlsson.skylight.android.lib.reversegeocoder.ReverseGeocodingResult
import se.gustavkarlsson.skylight.android.lib.scopedservice.ScopedService
import se.gustavkarlsson.skylight.android.lib.time.Time
import se.gustavkarlsson.skylight.android.lib.weather.Weather

@ExperimentalCoroutinesApi
internal class MainViewModel(
    private val mainKnot: Knot<State, Change>,
    auroraChanceEvaluator: ChanceEvaluator<CompleteAuroraReport>,
    relativeTimeFormatter: RelativeTimeFormatter,
    chanceLevelFormatter: Formatter<ChanceLevel>,
    darknessChanceEvaluator: ChanceEvaluator<Darkness>,
    darknessFormatter: Formatter<Darkness>,
    geomagLocationChanceEvaluator: ChanceEvaluator<GeomagLocation>,
    geomagLocationFormatter: Formatter<GeomagLocation>,
    kpIndexChanceEvaluator: ChanceEvaluator<KpIndex>,
    kpIndexFormatter: Formatter<KpIndex>,
    weatherChanceEvaluator: ChanceEvaluator<Weather>,
    weatherFormatter: Formatter<Weather>,
    private val permissionChecker: PermissionChecker,
    private val chanceToColorConverter: ChanceToColorConverter,
    time: Time,
    nowTextThreshold: Duration
) : ScopedService {

    override fun onCleared() {
        mainKnot.dispose()
    }

    fun resumeStreaming() = mainKnot.change.accept(Change.StreamToggle(true))

    fun pauseStreaming() = mainKnot.change.accept(Change.StreamToggle(false))

    private val stateFlow = mainKnot.state.asFlow()

    val toolbarTitleText: Flow<TextRef> = stateFlow
        .map {
            it.selectedPlace.name
        }
        .distinctUntilChanged()

    val chanceLevelText: Flow<TextRef> = stateFlow
        .map {
            it.selectedAuroraReport.toCompleteAuroraReport()
                ?.let(auroraChanceEvaluator::evaluate)
                ?: Chance.UNKNOWN
        }
        .map { ChanceLevel.fromChance(it) }
        .map { chanceLevelFormatter.format(it) }
        .distinctUntilChanged()

    private val timeSinceUpdate: Flow<Optional<String>> = stateFlow
        .map { it.selectedAuroraReport.timestamp.toOptional() }
        .flatMapLatest { time ->
            flow {
                while (true) {
                    emit(time)
                    delay(1000)
                }
            }
        }
        .map {
            it.map { timeSince ->
                relativeTimeFormatter.format(timeSince, time.now(), nowTextThreshold).toString()
            }
        }
        .distinctUntilChanged()

    private val locationName: Flow<Optional<String>> = stateFlow
        .map {
            when (val name = (it.selectedAuroraReport.locationName as? Loadable.Loaded)?.value) {
                is ReverseGeocodingResult.Success -> Present(name.name)
                else -> Absent
            }
        }

    val chanceSubtitleText: Flow<TextRef> =
        combine(timeSinceUpdate, locationName) { (time), (name) ->
            when {
                time == null -> TextRef.EMPTY
                name == null -> TextRef.string(time)
                else -> TextRef.stringRes(R.string.time_in_location, time, name)
            }
        }

    val darkness: Flow<FactorItem> = stateFlow
        .toFactorItems(
            LoadableAuroraReport::darkness,
            darknessChanceEvaluator::evaluate,
            darknessFormatter::format
        )

    val geomagLocation: Flow<FactorItem> = stateFlow
        .toFactorItems(
            LoadableAuroraReport::geomagLocation,
            geomagLocationChanceEvaluator::evaluate,
            geomagLocationFormatter::format
        )

    val kpIndex: Flow<FactorItem> = stateFlow
        .toFactorItems(
            LoadableAuroraReport::kpIndex,
            kpIndexChanceEvaluator::evaluate,
            kpIndexFormatter::format
        )

    val weather: Flow<FactorItem> = stateFlow
        .toFactorItems(
            LoadableAuroraReport::weather,
            weatherChanceEvaluator::evaluate,
            weatherFormatter::format
        )

    val errorBannerData: Flow<Optional<BannerData>> = stateFlow
        .map {
            when {
                it.selectedPlace != Place.Current -> Absent
                it.locationAccess == Access.Denied -> {
                    BannerData(
                        TextRef.stringRes(R.string.location_permission_denied_message),
                        TextRef.stringRes(R.string.fix),
                        R.drawable.ic_location_on,
                        BannerData.Event.RequestLocationPermission
                    ).toOptional()
                }
                it.locationAccess == Access.DeniedForever -> {
                    BannerData(
                        TextRef.stringRes(R.string.location_permission_denied_forever_message),
                        TextRef.stringRes(R.string.fix),
                        R.drawable.ic_warning,
                        BannerData.Event.OpenAppDetails
                    ).toOptional()
                }
                else -> Absent
            }
        }
        .distinctUntilChanged { a, b ->
            a.value?.message == b.value?.message &&
                a.value?.buttonText == b.value?.buttonText
        }

    private fun <T : Any> Flow<State>.toFactorItems(
        selectFactor: LoadableAuroraReport.() -> Loadable<Report<T>>,
        evaluate: (T) -> Chance,
        format: (T) -> TextRef
    ): Flow<FactorItem> =
        distinctUntilChanged()
            .map { it.selectedAuroraReport.selectFactor().toFactorItem(evaluate, format) }

    private fun <T : Any> Loadable<Report<T>>.toFactorItem(
        evaluate: (T) -> Chance,
        format: (T) -> TextRef
    ): FactorItem =
        when (this) {
            Loadable.Loading -> FactorItem.LOADING
            is Loadable.Loaded -> {
                when (val report = value) {
                    is Report.Success -> {
                        val valueText = format(report.value)
                        val chance = evaluate(report.value).value
                        FactorItem(
                            valueText,
                            R.color.on_surface,
                            chance,
                            chanceToColorConverter.convert(chance),
                            errorText = null
                        )
                    }
                    is Report.Error -> FactorItem(
                        valueText = TextRef.string("?"),
                        valueTextColor = R.color.error,
                        progress = null,
                        progressColor = ChanceToColorConverter.UNKNOWN_COLOR,
                        errorText = format(report.cause)
                    )
                    else -> error("Invalid report: $report")
                }
            }
        }

    fun refreshLocationPermission() = permissionChecker.refresh()
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
    val valueText: TextRef,
    @ColorRes val valueTextColor: Int,
    val progress: Double?,
    val progressColor: Int,
    val errorText: TextRef?
) {
    companion object {
        val LOADING = FactorItem(
            valueText = TextRef.string("…"),
            valueTextColor = R.color.on_surface_weaker,
            progress = null,
            progressColor = ChanceToColorConverter.UNKNOWN_COLOR,
            errorText = null
        )
    }
}

internal data class BannerData(
    val message: TextRef,
    val buttonText: TextRef,
    @DrawableRes val icon: Int,
    val buttonEvent: Event
) {
    enum class Event {
        RequestLocationPermission, OpenAppDetails
    }
}
