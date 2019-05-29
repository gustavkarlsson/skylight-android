package se.gustavkarlsson.skylight.android.feature.main

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import com.ioki.textref.TextRef
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Flowables
import org.threeten.bp.Duration
import se.gustavkarlsson.koptional.Absent
import se.gustavkarlsson.koptional.Optional
import se.gustavkarlsson.koptional.toOptional
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.Chance
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.entities.Place
import se.gustavkarlsson.skylight.android.entities.Report
import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.extensions.mapNotNull
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.krate.Command
import se.gustavkarlsson.skylight.android.entities.Permission
import se.gustavkarlsson.skylight.android.krate.SkylightStore
import se.gustavkarlsson.skylight.android.krate.State
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.Formatter
import se.gustavkarlsson.skylight.android.services.Time
import timber.log.Timber

internal class MainViewModel(
	private val store: SkylightStore,
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
	private val chanceToColorConverter: ChanceToColorConverter,
	time: Time,
	nowTextThreshold: Duration
) : ViewModel() {

	init {
		store.issue(Command.SelectPlace(Place.Current))
	}

	override fun onCleared() {
		store.issue(Command.SelectPlace(null))
	}

	val errorMessages: Flowable<Int> = store.states
		.mapNotNull { it.throwable }
		.map { throwable ->
			Timber.e(throwable)
			R.string.error_unknown_update_error
		}

	val toolbarTitleText: Flowable<TextRef> = store.states
		.mapNotNull {
			it.selectedPlace?.name
		}
		.distinctUntilChanged()

	val chanceLevelText: Flowable<TextRef> = store.states
		.map {
			it.selectedAuroraReport
				?.let(auroraChanceEvaluator::evaluate)
				?: Chance.UNKNOWN
		}
		.map(ChanceLevel.Companion::fromChance)
		.map(chanceLevelFormatter::format)
		.distinctUntilChanged()
		.distinctUntilChanged()

	private val timeSinceUpdate: Flowable<Optional<String>> = store.states
		.map { it.selectedAuroraReport?.timestamp.toOptional() }
		.switchMap { time ->
			Flowable.just(time)
				.repeatWhen { it.delay(1.seconds) }
		}
		.map {
			it.map { timeSince ->
				relativeTimeFormatter.format(timeSince, time.now().blockingGet(), nowTextThreshold).toString()
			}
		}
		.distinctUntilChanged()
		.observeOn(AndroidSchedulers.mainThread())

	private val locationName: Flowable<Optional<String>> = store.states
		.map { it.selectedAuroraReport?.locationName.toOptional() }

	val chanceSubtitleText: Flowable<TextRef> = Flowables
		.combineLatest(timeSinceUpdate, locationName) { (time), (name) ->
			when {
				time == null -> TextRef.EMPTY
				name == null -> TextRef(time)
				else -> TextRef(R.string.time_in_location, time, name)
			}
		}

	val darkness: Flowable<FactorItem> = store.states
		.toFactorItems(
			AuroraReport::darkness,
			darknessChanceEvaluator::evaluate,
			darknessFormatter::format
		)

	val geomagLocation: Flowable<FactorItem> = store.states
		.toFactorItems(
			AuroraReport::geomagLocation,
			geomagLocationChanceEvaluator::evaluate,
			geomagLocationFormatter::format
		)

	val kpIndex: Flowable<FactorItem> = store.states
		.toFactorItems(
			AuroraReport::kpIndex,
			kpIndexChanceEvaluator::evaluate,
			kpIndexFormatter::format
		)

	val weather: Flowable<FactorItem> = store.states
		.toFactorItems(
			AuroraReport::weather,
			weatherChanceEvaluator::evaluate,
			weatherFormatter::format
		)

	val errorBannerData: Flowable<Optional<BannerData>> = store.states
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

	private fun <T : Any> Flowable<State>.toFactorItems(
		selectFactor: AuroraReport.() -> Report<T>,
		evaluate: (T) -> Chance,
		format: (T) -> TextRef
	): Flowable<FactorItem> =
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

	fun refreshLocationPermission() =
		store.issue(Command.RefreshLocationPermission)

	fun signalLocationPermissionDeniedForever() =
		store.issue(Command.SignalLocationPermissionDeniedForever)
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
