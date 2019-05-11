package se.gustavkarlsson.skylight.android.gui.screens.main

import androidx.lifecycle.ViewModel
import com.ioki.textref.TextRef
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
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
import se.gustavkarlsson.skylight.android.entities.Report
import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.extensions.mapNotNull
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.krate.AuroraReportStreamCommand
import se.gustavkarlsson.skylight.android.krate.SkylightState
import se.gustavkarlsson.skylight.android.krate.SkylightStore
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.formatters.RelativeTimeFormatter
import se.gustavkarlsson.skylight.android.services.formatters.SingleValueFormatter
import se.gustavkarlsson.skylight.android.services.providers.Time
import se.gustavkarlsson.skylight.android.util.ChanceToColorConverter
import timber.log.Timber

class MainViewModel(
	private val store: SkylightStore,
	defaultLocationName: CharSequence,
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
		store.issue(AuroraReportStreamCommand(true))
	}

	val errorMessages: Flowable<Int> = store.states
		.mapNotNull { it.throwable }
		.map { throwable ->
			Timber.e(throwable)
			R.string.error_unknown_update_error
		}

	val locationName: Flowable<CharSequence> = store.states
		.map {
			it.currentPlace.auroraReport?.locationName ?: defaultLocationName
		}
		.distinctUntilChanged()

	val chanceLevel: Flowable<TextRef> = store.states
		.map {
			it.currentPlace.auroraReport
				?.let(auroraChanceEvaluator::evaluate)
				?: Chance.UNKNOWN
		}
		.map(ChanceLevel.Companion::fromChance)
		.map(chanceLevelFormatter::format)
		.distinctUntilChanged()

	val timeSinceUpdate: Flowable<CharSequence> = store.states
		.mapNotNull { it.currentPlace.auroraReport?.timestamp }
		.switchMap { time ->
			Flowable.just(time)
				.repeatWhen { it.delay(1.seconds) }
				.observeOn(AndroidSchedulers.mainThread())
		}
		.map {
			relativeTimeFormatter.format(it, time.now().blockingGet(), nowTextThreshold)
		}
		.distinctUntilChanged()

	val timeSinceUpdateVisibility: Flowable<Boolean> = store.states
		.mapNotNull { it.currentPlace.auroraReport?.timestamp }
		.map {
			when {
				it <= Instant.EPOCH -> false
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

	private fun <T : Any> Flowable<SkylightState>.toFactorItems(
		selectFactor: AuroraReport.() -> Report<T>,
		evaluate: (T) -> Chance,
		format: (T) -> TextRef
	): Flowable<FactorItem> =
		map { it.currentPlace.auroraReport?.selectFactor()?.value.toOptional() }
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

	override fun onCleared() {
		store.issue(AuroraReportStreamCommand(false))
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
