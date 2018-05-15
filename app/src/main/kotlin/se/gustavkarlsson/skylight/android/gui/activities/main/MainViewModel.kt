package se.gustavkarlsson.skylight.android.gui.activities.main

import com.hadisatrio.optional.Optional
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.ofType
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.actions.Action
import se.gustavkarlsson.skylight.android.actions.GetAuroraReportAction
import se.gustavkarlsson.skylight.android.actions.StreamAuroraReportsAction
import se.gustavkarlsson.skylight.android.actions.StreamConnectivityAction
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.gui.FluxViewModel
import se.gustavkarlsson.skylight.android.results.AuroraReportResult
import se.gustavkarlsson.skylight.android.results.ConnectivityResult
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
) : FluxViewModel<MainUiEvent, MainUiState, Action, Result>() {

	val refresh: Consumer<Unit> = Consumer {
		postEvent(RefreshEvent)
	}

	override fun createEventToActionTransformers(
	): Iterable<ObservableTransformer<in MainUiEvent, out Action>> =
		listOf(
			ObservableTransformer {
				it.ofType<RefreshEvent>()
					.map<Action> { GetAuroraReportAction }
					.startWith(GetAuroraReportAction)
			},
			ObservableTransformer {
				Observable.just(StreamAuroraReportsAction)
			},
			ObservableTransformer {
				Observable.just(StreamConnectivityAction)
			}
		)

	override fun createActionToResultTransformers(
	): Iterable<ObservableTransformer<in Action, out Result>> =
		listOf(
			ObservableTransformer {
				it.ofType<GetAuroraReportAction>().compose(refreshActionToResult)
			},
			ObservableTransformer {
				it.ofType<StreamAuroraReportsAction>().compose(streamAuroraReportsActionToResult)
			},
			ObservableTransformer {
				it.ofType<StreamConnectivityAction>().compose(streamConnectivityActionToResult)
			}
		)

	override fun createInitialState(): MainUiState = MainUiState()

	override fun accumulateState(last: MainUiState, result: Result): MainUiState =
		when (result) {
			is AuroraReportResult.Idle -> last.copy(
				throwable = null
			)
			is AuroraReportResult.InFlight -> last.copy(
				isRefreshing = true,
				throwable = null
			)
			is AuroraReportResult.Success -> last.copy(
				isRefreshing = false,
				auroraReport = result.auroraReport,
				throwable = null
			)
			is AuroraReportResult.Failure -> last.copy(
				isRefreshing = false,
				throwable = result.throwable
			)
			is ConnectivityResult -> last.copy(
				isConnectedToInternet = result.isConnectedToInternet
			)
		}

	private val refreshActionToResult =
		ObservableTransformer<GetAuroraReportAction, AuroraReportResult> {
			it.switchMap {
				auroraReportSingle
					.map<AuroraReportResult> { AuroraReportResult.Success(it) }
					.onErrorReturn { AuroraReportResult.Failure(it) }
					.toObservable()
					.startWith(AuroraReportResult.InFlight)
					.concatWith(Observable.just(AuroraReportResult.Idle))
			}
		}

	private val streamAuroraReportsActionToResult =
		ObservableTransformer<StreamAuroraReportsAction, AuroraReportResult> {
			it.switchMap {
				auroraReports
					.map<AuroraReportResult> { AuroraReportResult.Success(it) }
					.onErrorReturn { AuroraReportResult.Failure(it) }
					.toObservable()
					.startWith(AuroraReportResult.InFlight)
					.concatWith(Observable.just(AuroraReportResult.Idle))
			}
		}

	private val streamConnectivityActionToResult =
		ObservableTransformer<StreamConnectivityAction, ConnectivityResult> {
			it.switchMap {
				isConnectedToInternet
					.map(::ConnectivityResult)
					.toObservable()
			}
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
		.map(MainUiState::isConnectedToInternet)
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
		.map(MainUiState::isRefreshing)
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
}
