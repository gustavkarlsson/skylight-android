package se.gustavkarlsson.skylight.android.feature.main.gui

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import com.ioki.textref.TextRef
import de.halfbit.knot.Knot
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables
import org.threeten.bp.Duration
import se.gustavkarlsson.koptional.Absent
import se.gustavkarlsson.koptional.Optional
import se.gustavkarlsson.koptional.toOptional
import se.gustavkarlsson.skylight.android.entities.CompleteAuroraReport
import se.gustavkarlsson.skylight.android.entities.Chance
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.entities.Loadable
import se.gustavkarlsson.skylight.android.entities.LoadableAuroraReport
import se.gustavkarlsson.skylight.android.entities.Permission
import se.gustavkarlsson.skylight.android.entities.Report
import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.feature.main.ChanceToColorConverter
import se.gustavkarlsson.skylight.android.feature.main.Change
import se.gustavkarlsson.skylight.android.feature.main.R
import se.gustavkarlsson.skylight.android.feature.main.RelativeTimeFormatter
import se.gustavkarlsson.skylight.android.feature.main.State
import se.gustavkarlsson.skylight.android.services.PermissionChecker
import se.gustavkarlsson.skylight.android.entities.Place
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.Formatter
import se.gustavkarlsson.skylight.android.services.Time

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
) : ViewModel() {

	init {
		mainKnot.change.accept(Change.StreamToggle(true))
	}

	override fun onCleared() {
		mainKnot.change.accept(Change.StreamToggle(false))
	}

	val toolbarTitleText: Observable<TextRef> = mainKnot.state
		.map {
			it.selectedPlace.name
		}
		.distinctUntilChanged()

	val chanceLevelText: Observable<TextRef> = mainKnot.state
		.map {
			it.selectedAuroraReport.toCompleteAuroraReport()
				?.let(auroraChanceEvaluator::evaluate)
				?: Chance.UNKNOWN
		}
		.map(ChanceLevel.Companion::fromChance)
		.map(chanceLevelFormatter::format)
		.distinctUntilChanged()

	private val timeSinceUpdate: Observable<Optional<String>> = mainKnot.state
		.map { it.selectedAuroraReport.timestamp.toOptional() }
		.switchMap { time ->
			Observable.just(time)
				.repeatWhen { it.delay(1.seconds) }
		}
		.map {
			it.map { timeSince ->
				relativeTimeFormatter.format(timeSince, time.now(), nowTextThreshold).toString()
			}
		}
		.distinctUntilChanged()

	private val locationName: Observable<Optional<String>> = mainKnot.state
		.map { (it.selectedAuroraReport.locationName as? Loadable.Loaded)?.value.toOptional() }

	val chanceSubtitleText: Observable<TextRef> = Observables
		.combineLatest(timeSinceUpdate, locationName) { (time), (name) ->
			when {
				time == null -> TextRef.EMPTY
				name == null -> TextRef(time)
				else -> TextRef(R.string.time_in_location, time, name)
			}
		}
		.observeOn(AndroidSchedulers.mainThread())

	val darkness: Observable<FactorItem> = mainKnot.state
		.toFactorItems(
			LoadableAuroraReport::darkness,
			darknessChanceEvaluator::evaluate,
			darknessFormatter::format
		)

	val geomagLocation: Observable<FactorItem> = mainKnot.state
		.toFactorItems(
			LoadableAuroraReport::geomagLocation,
			geomagLocationChanceEvaluator::evaluate,
			geomagLocationFormatter::format
		)

	val kpIndex: Observable<FactorItem> = mainKnot.state
		.toFactorItems(
			LoadableAuroraReport::kpIndex,
			kpIndexChanceEvaluator::evaluate,
			kpIndexFormatter::format
		)

	val weather: Observable<FactorItem> = mainKnot.state
		.toFactorItems(
			LoadableAuroraReport::weather,
			weatherChanceEvaluator::evaluate,
			weatherFormatter::format
		)

	val errorBannerData: Observable<Optional<BannerData>> = mainKnot.state
		.map {
			when {
				it.selectedPlace != Place.Current -> Absent
				it.locationPermission == Permission.Denied -> {
					BannerData(
						TextRef(R.string.location_permission_denied_message),
						TextRef(R.string.fix),
						R.drawable.ic_location_on_white_24dp,
						BannerData.Event.RequestLocationPermission
					).toOptional()
				}
				it.locationPermission == Permission.DeniedForever -> {
					BannerData(
						TextRef(R.string.location_permission_denied_forever_message),
						TextRef(R.string.fix),
						R.drawable.ic_warning_white_24dp,
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

	private fun <T : Any> Observable<State>.toFactorItems(
		selectFactor: LoadableAuroraReport.() -> Loadable<Report<T>>,
		evaluate: (T) -> Chance,
		format: (T) -> TextRef
	): Observable<FactorItem> =
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
						FactorItem(valueText, chance, chanceToColorConverter.convert(chance))
					}
					is Report.Error -> FactorItem.ERROR
					else -> error("Invalid report: $report")
				}
			}
		}

	fun refreshLocationPermission() = permissionChecker.refresh()
}

internal data class FactorItem(
	val valueText: TextRef,
	val progress: Double?,
	val progressColor: Int
) {
	companion object {
		val LOADING = FactorItem(
			valueText = TextRef("?"),
			progress = null,
			progressColor = ChanceToColorConverter.UNKNOWN_COLOR
		)
		val ERROR = FactorItem( // TODO Replace with better error message
			valueText = TextRef("error"),
			progress = null,
			progressColor = ChanceToColorConverter.UNKNOWN_COLOR
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
