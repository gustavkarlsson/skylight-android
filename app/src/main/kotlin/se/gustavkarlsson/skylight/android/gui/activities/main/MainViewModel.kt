package se.gustavkarlsson.skylight.android.gui.activities.main

import com.hadisatrio.optional.Optional
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.*
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.ofType
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.actions.Action
import se.gustavkarlsson.skylight.android.actions.RefreshAction
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.gui.AutoDisposingViewModel
import se.gustavkarlsson.skylight.android.results.AuroraReportResult
import se.gustavkarlsson.skylight.android.results.Result
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.formatters.RelativeTimeFormatter
import se.gustavkarlsson.skylight.android.services.formatters.SingleValueFormatter
import se.gustavkarlsson.skylight.android.util.UserFriendlyException

class MainViewModel(
	auroraReportSingle: Single<AuroraReport>,
	auroraReports: Flowable<AuroraReport>,
	isConnectedToInternet: Flowable<Boolean>,
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
) : AutoDisposingViewModel() {

	private val errorRelay = PublishRelay.create<Throwable>()
	val errorMessages: Flowable<Int> = errorRelay
		.toFlowable(BackpressureStrategy.BUFFER)
		.map {
			if (it is UserFriendlyException) {
				it.stringResourceId
			} else {
				R.string.error_unknown_update_error
			}
		}

	val connectivityMessages: Flowable<Optional<CharSequence>> =
		Flowable.concat(Flowable.just(true), isConnectedToInternet)
			.map { connected ->
				if (connected) {
					Optional.absent()
				} else {
					Optional.of(notConnectedToInternetMessage)
				}
			}
			.distinctUntilChanged()

	val locationName: Flowable<CharSequence> = auroraReports
		.map {
			it.locationName ?: defaultLocationName
		}
		.distinctUntilChanged()

	private val timestamps = auroraReports
		.map(AuroraReport::timestamp)
		.distinctUntilChanged()
		.replay(1)
		.refCount()

	val chanceLevel: Flowable<CharSequence> = auroraReports
		.map(auroraChanceEvaluator::evaluate)
		.map(ChanceLevel.Companion::fromChance)
		.map(chanceLevelFormatter::format)
		.distinctUntilChanged()

	val timeSinceUpdate: Flowable<CharSequence> = timestamps
		.switchMap {
			Flowable.just(it)
				.repeatWhen { it.delay(1.seconds) }
		}
		.map {
			relativeTimeFormatter.format(it, now.blockingGet(), nowTextThreshold)
		}
		.distinctUntilChanged()

	val timeSinceUpdateVisibility: Flowable<Boolean> = timestamps
		.map {
			when {
				it <= Instant.EPOCH -> false
				else -> true
			}
		}
		.distinctUntilChanged()

	val darknessValue: Flowable<CharSequence> = auroraReports.map { it.factors.darkness }
		.map(darknessFormatter::format)
		.distinctUntilChanged()

	val darknessChance: Flowable<Chance> = auroraReports.map { it.factors.darkness }
		.map(darknessChanceEvaluator::evaluate)
		.distinctUntilChanged()

	val geomagLocationValue: Flowable<CharSequence> =
		auroraReports.map { it.factors.geomagLocation }
			.map(geomagLocationFormatter::format)
			.distinctUntilChanged()

	val geomagLocationChance: Flowable<Chance> = auroraReports.map { it.factors.geomagLocation }
		.map(geomagLocationChanceEvaluator::evaluate)
		.distinctUntilChanged()

	val kpIndexValue: Flowable<CharSequence> = auroraReports.map { it.factors.kpIndex }
		.map(kpIndexFormatter::format)
		.distinctUntilChanged()

	val kpIndexChance: Flowable<Chance> = auroraReports.map { it.factors.kpIndex }
		.map(kpIndexChanceEvaluator::evaluate)
		.distinctUntilChanged()

	val visibilityValue: Flowable<CharSequence> = auroraReports.map { it.factors.visibility }
		.map(visibilityFormatter::format)
		.distinctUntilChanged()

	val visibilityChance: Flowable<Chance> = auroraReports.map { it.factors.visibility }
		.map(visibilityChanceEvaluator::evaluate)
		.distinctUntilChanged()

	private val initialState: MainUiState = MainUiState(
		defaultLocationName,
		false,
		null,
		null,
		chanceLevelFormatter.format(ChanceLevel.UNKNOWN),
		null,
		MainUiState.Factor(darknessFormatter.format(Darkness()), Chance.UNKNOWN),
		MainUiState.Factor(geomagLocationFormatter.format(GeomagLocation()), Chance.UNKNOWN),
		MainUiState.Factor(kpIndexFormatter.format(KpIndex()), Chance.UNKNOWN),
		MainUiState.Factor(visibilityFormatter.format(Visibility()), Chance.UNKNOWN)
	)

	private val events = PublishRelay.create<MainUiEvent>()
	val onEvent: Consumer<MainUiEvent> = events

	private val actions: Observable<out Action> = events
		.publish {
			// Merge if multiple event types
			it.ofType<RefreshEvent>().map { RefreshAction }
		}

	private val results: Observable<out Result> = actions
		.publish {
			// Merge if multiple action types
			it.ofType<RefreshAction>().startWith(RefreshAction).compose(refreshActionToResult)
		}

	private val refreshActionToResult = ObservableTransformer<RefreshAction, AuroraReportResult> {
		it.switchMap {
			auroraReportSingle
				.map<AuroraReportResult> { AuroraReportResult.Success(it) }
				.onErrorReturn { AuroraReportResult.Failure(it) }
				// TODO Consider .observeOn(AndroidSchedulers.mainThread())
				.toObservable()
				.startWith(AuroraReportResult.InFlight)
		}
	}

	val states: Observable<MainUiState> = results
		.scan(initialState) { lastState, result ->
			when (result) {
				is AuroraReportResult.InFlight -> lastState.copy(isRefreshing = true)
				is AuroraReportResult.Success -> lastState.copy(isRefreshing = false)
				is AuroraReportResult.Failure -> lastState.copy(isRefreshing = false)
			}
		}
		.replay(1)
		.refCount()

	init {
	    states.subscribe().autoDisposeOnCleared()
	}
}
