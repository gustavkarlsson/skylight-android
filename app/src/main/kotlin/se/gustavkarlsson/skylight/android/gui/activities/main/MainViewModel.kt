package se.gustavkarlsson.skylight.android.gui.activities.main

import android.arch.lifecycle.ViewModel
import com.hadisatrio.optional.Optional
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.flux.AuroraReportStreamAction
import se.gustavkarlsson.skylight.android.flux.GetAuroraReportAction
import se.gustavkarlsson.skylight.android.flux.SkylightStore
import se.gustavkarlsson.skylight.android.flux.State
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
	visibilityChanceEvaluator: ChanceEvaluator<Visibility>,
	visibilityFormatter: SingleValueFormatter<Visibility>,
	now: Single<Instant>,
	nowTextThreshold: Duration
) : ViewModel() {

	init {
		store.postAction(AuroraReportStreamAction(true))
	}

	private val states = store.states.observeOn(AndroidSchedulers.mainThread())

	val swipedToRefresh: Consumer<Unit> = Consumer {
		store.postAction(GetAuroraReportAction)
	}

	val errorMessages: Observable<Int> = states
		.filter { it.throwable != null }
		.map {
			if (it.throwable is UserFriendlyException) {
				it.throwable.stringResourceId
			} else {
				R.string.error_unknown_update_error
			}
		}

	val connectivityMessages: Observable<Optional<CharSequence>> = states
		.map(State::isConnectedToInternet)
		.map { connected ->
			if (connected) {
				Optional.absent()
			} else {
				Optional.of(notConnectedToInternetMessage)
			}
		}
		.distinctUntilChanged()

	val locationName: Observable<CharSequence> = states
		.map {
			it.auroraReport?.locationName ?: defaultLocationName
		}
		.distinctUntilChanged()

	val isRefreshing: Observable<Boolean> = states
		.map(State::isRefreshing)
		.distinctUntilChanged()

	val chanceLevel: Observable<CharSequence> = states
		.map {
			it.auroraReport
				?.let(auroraChanceEvaluator::evaluate)
				?: Chance.UNKNOWN
		}
		.map(ChanceLevel.Companion::fromChance)
		.map(chanceLevelFormatter::format)
		.distinctUntilChanged()

	val timeSinceUpdate: Observable<CharSequence> = states
		.filter { it.auroraReport != null }
		.map { it.auroraReport!!.timestamp }
		.switchMap {
			Observable.just(it)
				.repeatWhen { it.delay(1.seconds) }
				.observeOn(AndroidSchedulers.mainThread())
		}
		.map {
			relativeTimeFormatter.format(it, now.blockingGet(), nowTextThreshold)
		}
		.distinctUntilChanged()

	val timeSinceUpdateVisibility: Observable<Boolean> = states
		.filter { it.auroraReport != null }
		.map { it.auroraReport!!.timestamp }
		.map {
			when {
				it <= Instant.EPOCH -> false
				else -> true
			}
		}
		.distinctUntilChanged()

	val darknessValue: Observable<CharSequence> = states
		.map {
			it.auroraReport
				?.let(AuroraReport::factors)
				?.let(AuroraFactors::darkness)
				?.let(darknessFormatter::format)
				?: "?"
		}
		.distinctUntilChanged()

	val darknessChance: Observable<Chance> = states
		.map {
			it.auroraReport
				?.let(AuroraReport::factors)
				?.let(AuroraFactors::darkness)
				?.let(darknessChanceEvaluator::evaluate)
				?: Chance.UNKNOWN
		}
		.distinctUntilChanged()

	val geomagLocationValue: Observable<CharSequence> = states
		.map {
			it.auroraReport
				?.let(AuroraReport::factors)
				?.let(AuroraFactors::geomagLocation)
				?.let(geomagLocationFormatter::format)
				?: "?"
		}
		.distinctUntilChanged()

	val geomagLocationChance: Observable<Chance> = states
		.map {
			it.auroraReport
				?.let(AuroraReport::factors)
				?.let(AuroraFactors::geomagLocation)
				?.let(geomagLocationChanceEvaluator::evaluate)
				?: Chance.UNKNOWN
		}
		.distinctUntilChanged()

	val kpIndexValue: Observable<CharSequence> = states
		.map {
			it.auroraReport
				?.let(AuroraReport::factors)
				?.let(AuroraFactors::kpIndex)
				?.let(kpIndexFormatter::format)
				?: "?"
		}
		.distinctUntilChanged()

	val kpIndexChance: Observable<Chance> = states
		.map {
			it.auroraReport
				?.let(AuroraReport::factors)
				?.let(AuroraFactors::kpIndex)
				?.let(kpIndexChanceEvaluator::evaluate)
				?: Chance.UNKNOWN
		}
		.distinctUntilChanged()

	val visibilityValue: Observable<CharSequence> = states
		.map {
			it.auroraReport
				?.let(AuroraReport::factors)
				?.let(AuroraFactors::visibility)
				?.let(visibilityFormatter::format)
				?: "?"
		}
		.distinctUntilChanged()

	val visibilityChance: Observable<Chance> = states
		.map {
			it.auroraReport
				?.let(AuroraReport::factors)
				?.let(AuroraFactors::visibility)
				?.let(visibilityChanceEvaluator::evaluate)
				?: Chance.UNKNOWN
		}
		.distinctUntilChanged()

	override fun onCleared() {
		store.postAction(AuroraReportStreamAction(false))
	}
}
