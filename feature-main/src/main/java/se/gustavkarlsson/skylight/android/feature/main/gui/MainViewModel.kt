package se.gustavkarlsson.skylight.android.feature.main.gui

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import com.ioki.textref.TextRef
import com.jakewharton.rxrelay2.PublishRelay
import de.halfbit.knot.Knot
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables
import org.threeten.bp.Duration
import se.gustavkarlsson.koptional.Absent
import se.gustavkarlsson.koptional.Optional
import se.gustavkarlsson.koptional.toOptional
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.Chance
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.feature.main.Change
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.entities.Permission
import se.gustavkarlsson.skylight.android.entities.Place
import se.gustavkarlsson.skylight.android.entities.Report
import se.gustavkarlsson.skylight.android.feature.main.State
import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.feature.main.ChanceToColorConverter
import se.gustavkarlsson.skylight.android.feature.main.R
import se.gustavkarlsson.skylight.android.feature.main.RelativeTimeFormatter
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionChecker
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.Formatter
import se.gustavkarlsson.skylight.android.services.Time

internal class MainViewModel(
	private val mainKnot: Knot<State, Change>,
	auroraChanceEvaluator: ChanceEvaluator<AuroraReport>,
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
		mainKnot.change.accept(Change.Stream(true))
	}

	override fun onCleared() {
		mainKnot.change.accept(Change.Stream(false))
	}

	// FIXME what about errors?

	val toolbarTitleText: Observable<TextRef> = mainKnot.state
		.map {
			it.selectedPlace?.name ?: TextRef.EMPTY
		}
		.distinctUntilChanged()

	val chanceLevelText: Observable<TextRef> = mainKnot.state
		.map {
			it.selectedAuroraReport
				?.let(auroraChanceEvaluator::evaluate)
				?: Chance.UNKNOWN
		}
		.map(ChanceLevel.Companion::fromChance)
		.map(chanceLevelFormatter::format)
		.distinctUntilChanged()
		.distinctUntilChanged()

	private val timeSinceUpdate: Observable<Optional<String>> = mainKnot.state
		.map { it.selectedAuroraReport?.timestamp.toOptional() }
		.switchMap { time ->
			Observable.just(time)
				.repeatWhen { it.delay(1.seconds) }
		}
		.map {
			it.map { timeSince ->
				relativeTimeFormatter.format(timeSince, time.now().blockingGet(), nowTextThreshold).toString()
			}
		}
		.distinctUntilChanged()
		.observeOn(AndroidSchedulers.mainThread())

	private val locationName: Observable<Optional<String>> = mainKnot.state
		.map { it.selectedAuroraReport?.locationName.toOptional() }

	val chanceSubtitleText: Observable<TextRef> = Observables
		.combineLatest(timeSinceUpdate, locationName) { (time), (name) ->
			when {
				time == null -> TextRef.EMPTY
				name == null -> TextRef(time)
				else -> TextRef(R.string.time_in_location, time, name)
			}
		}

	val darkness: Observable<FactorItem> = mainKnot.state
		.toFactorItems(
			AuroraReport::darkness,
			darknessChanceEvaluator::evaluate,
			darknessFormatter::format
		)

	val geomagLocation: Observable<FactorItem> = mainKnot.state
		.toFactorItems(
			AuroraReport::geomagLocation,
			geomagLocationChanceEvaluator::evaluate,
			geomagLocationFormatter::format
		)

	val kpIndex: Observable<FactorItem> = mainKnot.state
		.toFactorItems(
			AuroraReport::kpIndex,
			kpIndexChanceEvaluator::evaluate,
			kpIndexFormatter::format
		)

	val weather: Observable<FactorItem> = mainKnot.state
		.toFactorItems(
			AuroraReport::weather,
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
						{ requestLocationPermissionRelay.accept(Unit) },
						R.drawable.ic_location_on_white_24dp
					).toOptional()
				}
				it.locationPermission == Permission.DeniedForever -> {
					BannerData(
						TextRef(R.string.location_permission_denied_forever_message),
						TextRef(R.string.fix),
						{ openAppDetailsRelay.accept(Unit) },
						R.drawable.ic_warning_white_24dp
					).toOptional()
				}
				else -> Absent
			}
		}
		.distinctUntilChanged { a, b ->
			a.value?.message == b.value?.message &&
				a.value?.buttonText == b.value?.buttonText
		}

	private val requestLocationPermissionRelay = PublishRelay.create<Unit>()
	val requestLocationPermission: Observable<Unit> = requestLocationPermissionRelay

	private val openAppDetailsRelay = PublishRelay.create<Unit>()
	val openAppDetails: Observable<Unit> = openAppDetailsRelay

	private fun <T : Any> Observable<State>.toFactorItems(
		selectFactor: AuroraReport.() -> Report<T>,
		evaluate: (T) -> Chance,
		format: (T) -> TextRef
	): Observable<FactorItem> =
		map { it.selectedAuroraReport?.selectFactor()?.value.toOptional() }
			.distinctUntilChanged()
			.map { it.toFactorItem(evaluate, format) }

	private fun <T : Any> Optional<T>.toFactorItem(
		evaluate: (T) -> Chance,
		format: (T) -> TextRef
	): FactorItem {
		val factor = value
		return if (factor == null) {
			FactorItem.EMPTY
		} else {
			val chance = evaluate(factor).value
			FactorItem(
				valueText = format(factor),
				progress = chance,
				progressColor = chanceToColorConverter.convert(chance)
			)
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
		val EMPTY = FactorItem(
			valueText = TextRef("?"),
			progress = null,
			progressColor = ChanceToColorConverter.UNKNOWN_COLOR
		)
	}
}

internal data class BannerData(
	val message: TextRef,
	val buttonText: TextRef,
	val buttonAction: () -> Unit,
	@DrawableRes val icon: Int
)
