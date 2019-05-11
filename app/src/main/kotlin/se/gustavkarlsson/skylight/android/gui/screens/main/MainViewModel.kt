package se.gustavkarlsson.skylight.android.gui.screens.main

import androidx.lifecycle.ViewModel
import com.ioki.textref.TextRef
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Flowables
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import se.gustavkarlsson.koptional.Optional
import se.gustavkarlsson.koptional.toOptional
import se.gustavkarlsson.skylight.android.R
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
import se.gustavkarlsson.skylight.android.krate.SkylightStore
import se.gustavkarlsson.skylight.android.krate.State
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.formatters.RelativeTimeFormatter
import se.gustavkarlsson.skylight.android.services.formatters.SingleValueFormatter
import se.gustavkarlsson.skylight.android.services.providers.Time
import se.gustavkarlsson.skylight.android.util.ChanceToColorConverter
import timber.log.Timber

class MainViewModel(
	private val store: SkylightStore,
	auroraChanceEvaluator: ChanceEvaluator<AuroraReport>,
	relativeTimeFormatter: RelativeTimeFormatter,
	chanceLevelFormatter: SingleValueFormatter<ChanceLevel>,
	darknessChanceEvaluator: ChanceEvaluator<Darkness>,
	darknessFormatter: SingleValueFormatter<Darkness>,
	geomagLocationChanceEvaluator: ChanceEvaluator<GeomagLocation>,
	geomagLocationFormatter: SingleValueFormatter<GeomagLocation>,
	kpIndexChanceEvaluator: ChanceEvaluator<KpIndex>,
	kpIndexFormatter: SingleValueFormatter<KpIndex>,
	weatherChanceEvaluator: ChanceEvaluator<Weather>,
	weatherFormatter: SingleValueFormatter<Weather>,
	private val chanceToColorConverter: ChanceToColorConverter,
	time: Time,
	nowTextThreshold: Duration
) : ViewModel() {

	init {
		store.issue(Command.SelectPlace(Place.Current.ID))
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
			it.selectedPlace?.auroraReport
				?.let(auroraChanceEvaluator::evaluate)
				?: Chance.UNKNOWN
		}
		.map(ChanceLevel.Companion::fromChance)
		.map(chanceLevelFormatter::format)
		.distinctUntilChanged()
		.distinctUntilChanged()

	private val timeSinceUpdate: Flowable<Optional<String>> = store.states
		.map { it.selectedPlace?.auroraReport?.timestamp.toOptional() }
		.switchMap { time ->
			Flowable.just(time)
				.repeatWhen { it.delay(1.seconds) }
				.observeOn(AndroidSchedulers.mainThread())
		}
		.map {
			it.map { timeSince ->
				relativeTimeFormatter.format(timeSince, time.now().blockingGet(), nowTextThreshold).toString()
			}
		}
		.distinctUntilChanged()

	private val locationName: Flowable<Optional<String>> = store.states
		.map { it.selectedPlace?.auroraReport?.locationName.toOptional() }

	val chanceSubtitleText: Flowable<TextRef> = Flowables
		.combineLatest(timeSinceUpdate, locationName) { (time), (name) ->
			when {
				time == null -> TextRef.EMPTY
				name == null -> TextRef(time)
				else -> TextRef(R.string.time_in_location, time, name)
			}
		}

	val chanceSubtitleVisibility: Flowable<Boolean> = store.states
		.map {
			val timestamp = it.selectedPlace?.auroraReport?.timestamp ?: Instant.MIN
			when {
				timestamp == null -> false
				timestamp <= Instant.EPOCH -> false
				else -> true
			}
		}
		.distinctUntilChanged()

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

	private fun <T : Any> Flowable<State>.toFactorItems(
		selectFactor: AuroraReport.() -> Report<T>,
		evaluate: (T) -> Chance,
		format: (T) -> TextRef
	): Flowable<FactorItem> =
		map { it.selectedPlace?.auroraReport?.selectFactor()?.value.toOptional() }
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
}

data class FactorItem(
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
