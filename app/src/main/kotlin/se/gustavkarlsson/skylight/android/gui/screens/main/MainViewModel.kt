package se.gustavkarlsson.skylight.android.gui.screens.main

import androidx.lifecycle.ViewModel
import com.hadisatrio.optional.Optional
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.krate.*
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.formatters.RelativeTimeFormatter
import se.gustavkarlsson.skylight.android.services.formatters.SingleValueFormatter
import se.gustavkarlsson.skylight.android.util.UserFriendlyException

class MainViewModel(
	private val store: SkylightStore,
	defaultLocationName: CharSequence,
	notConnectedToInternetMessage: CharSequence,
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
	now: Single<Instant>,
	nowTextThreshold: Duration
) : ViewModel() {

	init {
		store.issue(AuroraReportStreamCommand(true))
	}

	val swipedToRefresh: Consumer<Unit> = Consumer {
		store.issue(GetAuroraReportCommand)
	}

	val errorMessages: Flowable<Int> = store.states
		.filter { it.throwable != null }
		.map {
			val throwable = it.throwable
			if (throwable is UserFriendlyException) {
				throwable.stringResourceId
			} else {
				R.string.error_unknown_update_error
			}
		}

	val connectivityMessages: Flowable<Optional<CharSequence>> = store.states
		.map(SkylightState::isConnectedToInternet)
		.map { connected ->
			if (connected) {
				Optional.absent()
			} else {
				Optional.of(notConnectedToInternetMessage)
			}
		}
		.distinctUntilChanged()

	val locationName: Flowable<CharSequence> = store.states
		.map {
			it.auroraReport?.locationName ?: defaultLocationName
		}
		.distinctUntilChanged()

	val isRefreshing: Flowable<Boolean> = store.states
		.map(SkylightState::isRefreshing)
		.distinctUntilChanged()

	val chanceLevel: Flowable<CharSequence> = store.states
		.map {
			it.auroraReport
				?.let(auroraChanceEvaluator::evaluate)
				?: Chance.UNKNOWN
		}
		.map(ChanceLevel.Companion::fromChance)
		.map(chanceLevelFormatter::format)
		.distinctUntilChanged()

	val timeSinceUpdate: Flowable<CharSequence> = store.states
		.filter { it.auroraReport != null }
		.map { it.auroraReport!!.timestamp }
		.switchMap {
			Flowable.just(it)
				.repeatWhen { it.delay(1.seconds) }
				.observeOn(AndroidSchedulers.mainThread())
		}
		.map {
			relativeTimeFormatter.format(it, now.blockingGet(), nowTextThreshold)
		}
		.distinctUntilChanged()

	val timeSinceUpdateVisibility: Flowable<Boolean> = store.states
		.filter { it.auroraReport != null }
		.map { it.auroraReport!!.timestamp }
		.map {
			when {
				it <= Instant.EPOCH -> false
				else -> true
			}
		}
		.distinctUntilChanged()

	val darknessValue: Flowable<CharSequence> = store.states
		.map {
			it.auroraReport
				?.let(AuroraReport::factors)
				?.let(AuroraFactors::darkness)
				?.let(darknessFormatter::format)
				?: "?"
		}
		.distinctUntilChanged()

	val darknessChance: Flowable<Chance> = store.states
		.map {
			it.auroraReport
				?.let(AuroraReport::factors)
				?.let(AuroraFactors::darkness)
				?.let(darknessChanceEvaluator::evaluate)
				?: Chance.UNKNOWN
		}
		.distinctUntilChanged()

	val geomagLocationValue: Flowable<CharSequence> = store.states
		.map {
			it.auroraReport
				?.let(AuroraReport::factors)
				?.let(AuroraFactors::geomagLocation)
				?.let(geomagLocationFormatter::format)
				?: "?"
		}
		.distinctUntilChanged()

	val geomagLocationChance: Flowable<Chance> = store.states
		.map {
			it.auroraReport
				?.let(AuroraReport::factors)
				?.let(AuroraFactors::geomagLocation)
				?.let(geomagLocationChanceEvaluator::evaluate)
				?: Chance.UNKNOWN
		}
		.distinctUntilChanged()

	val kpIndexValue: Flowable<CharSequence> = store.states
		.map {
			it.auroraReport
				?.let(AuroraReport::factors)
				?.let(AuroraFactors::kpIndex)
				?.let(kpIndexFormatter::format)
				?: "?"
		}
		.distinctUntilChanged()

	val kpIndexChance: Flowable<Chance> = store.states
		.map {
			it.auroraReport
				?.let(AuroraReport::factors)
				?.let(AuroraFactors::kpIndex)
				?.let(kpIndexChanceEvaluator::evaluate)
				?: Chance.UNKNOWN
		}
		.distinctUntilChanged()

	val weatherValue: Flowable<CharSequence> = store.states
		.map {
			it.auroraReport
				?.let(AuroraReport::factors)
				?.let(AuroraFactors::weather)
				?.let(weatherFormatter::format)
				?: "?"
		}
		.distinctUntilChanged()

	val weatherChance: Flowable<Chance> = store.states
		.map {
			it.auroraReport
				?.let(AuroraReport::factors)
				?.let(AuroraFactors::weather)
				?.let(weatherChanceEvaluator::evaluate)
				?: Chance.UNKNOWN
		}
		.distinctUntilChanged()

	override fun onCleared() {
		store.issue(AuroraReportStreamCommand(false))
	}
}
