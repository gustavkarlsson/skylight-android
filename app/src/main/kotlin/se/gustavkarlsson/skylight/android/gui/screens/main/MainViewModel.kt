package se.gustavkarlsson.skylight.android.gui.screens.main

import androidx.lifecycle.ViewModel
import com.ioki.textref.TextRef
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.Chance
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.extensions.mapNotNull
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.krate.AuroraReportStreamCommand
import se.gustavkarlsson.skylight.android.krate.SkylightStore
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.formatters.RelativeTimeFormatter
import se.gustavkarlsson.skylight.android.services.formatters.SingleValueFormatter
import se.gustavkarlsson.skylight.android.services.providers.Time
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
	time: Time,
	nowTextThreshold: Duration
) : ViewModel() {

	init {
		store.issue(AuroraReportStreamCommand(true))
	}

	val errorMessages: Flowable<Int> = store.states
		.filter { it.throwable != null }
		.map {
			val throwable = it.throwable
			Timber.e(throwable)
			R.string.error_unknown_update_error
		}

	val locationName: Flowable<CharSequence> = store.states
		.map {
			it.currentLocationAuroraReport?.locationName ?: defaultLocationName
		}
		.distinctUntilChanged()

	val chanceLevel: Flowable<TextRef> = store.states
		.map {
			it.currentLocationAuroraReport
				?.let(auroraChanceEvaluator::evaluate)
				?: Chance.UNKNOWN
		}
		.map(ChanceLevel.Companion::fromChance)
		.map(chanceLevelFormatter::format)
		.distinctUntilChanged()

	val timeSinceUpdate: Flowable<CharSequence> = store.states
		.mapNotNull { it.currentLocationAuroraReport?.timestamp }
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
		.mapNotNull { it.currentLocationAuroraReport?.timestamp }
		.map {
			when {
				it <= Instant.EPOCH -> false
				else -> true
			}
		}
		.distinctUntilChanged()

	val darknessValue: Flowable<TextRef> = store.states
		.map {
			it.currentLocationAuroraReport
				?.let(AuroraReport::darkness)
				?.value
				?.let(darknessFormatter::format)
				?: TextRef("?")
		}
		.distinctUntilChanged()

	val darknessChance: Flowable<Chance> = store.states
		.map {
			it.currentLocationAuroraReport
				?.let(AuroraReport::darkness)
				?.value
				?.let(darknessChanceEvaluator::evaluate)
				?: Chance.UNKNOWN
		}
		.distinctUntilChanged()

	val geomagLocationValue: Flowable<TextRef> = store.states
		.map {
			it.currentLocationAuroraReport
				?.let(AuroraReport::geomagLocation)
				?.value
				?.let(geomagLocationFormatter::format)
				?: TextRef("?")
		}
		.distinctUntilChanged()

	val geomagLocationChance: Flowable<Chance> = store.states
		.map {
			it.currentLocationAuroraReport
				?.let(AuroraReport::geomagLocation)
				?.value
				?.let(geomagLocationChanceEvaluator::evaluate)
				?: Chance.UNKNOWN
		}
		.distinctUntilChanged()

	val kpIndexValue: Flowable<TextRef> = store.states
		.map {
			it.currentLocationAuroraReport
				?.let(AuroraReport::kpIndex)
				?.value
				?.let(kpIndexFormatter::format)
				?: TextRef("?")
		}
		.distinctUntilChanged()

	val kpIndexChance: Flowable<Chance> = store.states
		.map {
			it.currentLocationAuroraReport
				?.let(AuroraReport::kpIndex)
				?.value
				?.let(kpIndexChanceEvaluator::evaluate)
				?: Chance.UNKNOWN
		}
		.distinctUntilChanged()

	val weatherValue: Flowable<TextRef> = store.states
		.map {
			it.currentLocationAuroraReport
				?.let(AuroraReport::weather)
				?.value
				?.let(weatherFormatter::format)
				?: TextRef("?")
		}
		.distinctUntilChanged()

	val weatherChance: Flowable<Chance> = store.states
		.map {
			it.currentLocationAuroraReport
				?.let(AuroraReport::weather)
				?.value
				?.let(weatherChanceEvaluator::evaluate)
				?: Chance.UNKNOWN
		}
		.distinctUntilChanged()

	override fun onCleared() {
		store.issue(AuroraReportStreamCommand(false))
	}
}
