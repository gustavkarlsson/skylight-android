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
import se.gustavkarlsson.skylight.android.flux.*
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
		store.postAction(GetAuroraReportAction)
		store.postAction(AuroraReportStreamAction(true))
	}

	val swipedToRefresh: Consumer<Unit> = Consumer {
		store.postAction(GetAuroraReportAction)
	}

	val errorMessages: Observable<Int> = store.getState()
		.filter { it.throwable != null }
		.map {
			if (it.throwable is UserFriendlyException) {
				it.throwable.stringResourceId
			} else {
				R.string.error_unknown_update_error
			}
		}

	val connectivityMessages: Observable<Optional<CharSequence>> = store.getState()
		.map(SkylightState::isConnectedToInternet)
		.map { connected ->
			if (connected) {
				Optional.absent()
			} else {
				Optional.of(notConnectedToInternetMessage)
			}
		}
		.distinctUntilChanged()

	val locationName: Observable<CharSequence> = store.getState()
		.map {
			it.auroraReport?.locationName ?: defaultLocationName
		}
		.distinctUntilChanged()

	val isRefreshing: Observable<Boolean> = store.getState()
		.map(SkylightState::isRefreshing)
		.distinctUntilChanged()

	val chanceLevel: Observable<CharSequence> = store.getState()
		.map {
			it.auroraReport
				?.let(auroraChanceEvaluator::evaluate)
				?: Chance.UNKNOWN
		}
		.map(ChanceLevel.Companion::fromChance)
		.map(chanceLevelFormatter::format)
		.distinctUntilChanged()

	val timeSinceUpdate: Observable<CharSequence> = store.getState()
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

	val timeSinceUpdateVisibility: Observable<Boolean> = store.getState()
		.filter { it.auroraReport != null }
		.map { it.auroraReport!!.timestamp }
		.map {
			when {
				it <= Instant.EPOCH -> false
				else -> true
			}
		}
		.distinctUntilChanged()

	val darknessValue: Observable<CharSequence> = store.getState()
		.map {
			it.auroraReport
				?.let(AuroraReport::factors)
				?.let(AuroraFactors::darkness)
				?.let(darknessFormatter::format)
				?: "?"
		}
		.distinctUntilChanged()

	val darknessChance: Observable<Chance> = store.getState()
		.map {
			it.auroraReport
				?.let(AuroraReport::factors)
				?.let(AuroraFactors::darkness)
				?.let(darknessChanceEvaluator::evaluate)
				?: Chance.UNKNOWN
		}
		.distinctUntilChanged()

	val geomagLocationValue: Observable<CharSequence> = store.getState()
		.map {
			it.auroraReport
				?.let(AuroraReport::factors)
				?.let(AuroraFactors::geomagLocation)
				?.let(geomagLocationFormatter::format)
				?: "?"
		}
		.distinctUntilChanged()

	val geomagLocationChance: Observable<Chance> = store.getState()
		.map {
			it.auroraReport
				?.let(AuroraReport::factors)
				?.let(AuroraFactors::geomagLocation)
				?.let(geomagLocationChanceEvaluator::evaluate)
				?: Chance.UNKNOWN
		}
		.distinctUntilChanged()

	val kpIndexValue: Observable<CharSequence> = store.getState()
		.map {
			it.auroraReport
				?.let(AuroraReport::factors)
				?.let(AuroraFactors::kpIndex)
				?.let(kpIndexFormatter::format)
				?: "?"
		}
		.distinctUntilChanged()

	val kpIndexChance: Observable<Chance> = store.getState()
		.map {
			it.auroraReport
				?.let(AuroraReport::factors)
				?.let(AuroraFactors::kpIndex)
				?.let(kpIndexChanceEvaluator::evaluate)
				?: Chance.UNKNOWN
		}
		.distinctUntilChanged()

	val visibilityValue: Observable<CharSequence> = store.getState()
		.map {
			it.auroraReport
				?.let(AuroraReport::factors)
				?.let(AuroraFactors::visibility)
				?.let(visibilityFormatter::format)
				?: "?"
		}
		.distinctUntilChanged()

	val visibilityChance: Observable<Chance> = store.getState()
		.map {
			it.auroraReport
				?.let(AuroraReport::factors)
				?.let(AuroraFactors::visibility)
				?.let(visibilityChanceEvaluator::evaluate)
				?: Chance.UNKNOWN
		}
		.distinctUntilChanged()

	val hideDialogClicked: Consumer<Unit> = Consumer {
		store.postAction(HideDialogAction)
	}

	val darknessFactorClicked: Consumer<Unit> = Consumer {
		store.postAction(
			ShowDialogAction(R.string.factor_darkness_title_full, R.string.factor_darkness_desc))
	}

	val geomagLocationFactorClicked: Consumer<Unit> = Consumer {
		store.postAction(
			ShowDialogAction(R.string.factor_geomag_location_title_full, R.string.factor_geomag_location_desc))
	}

	val kpIndexFactorClicked: Consumer<Unit> = Consumer {
		store.postAction(
			ShowDialogAction(R.string.factor_kp_index_title_full, R.string.factor_kp_index_desc))
	}

	val visibilityFactorClicked: Consumer<Unit> = Consumer {
		store.postAction(
			ShowDialogAction(R.string.factor_visibility_title_full, R.string.factor_visibility_desc))
	}

	val showDialog: Observable<SkylightState.Dialog> = store.getState()
		.distinctUntilChanged { last, new ->
			last.dialog == new.dialog
		}
		.filter { it.dialog != null }
		.map { it.dialog!! }

	val hideDialog: Observable<Unit> = store.getState()
		.distinctUntilChanged { last, new ->
			last.dialog == new.dialog
		}
		.filter { it.dialog == null }
		.map { Unit }

	override fun onCleared() {
		store.postAction(AuroraReportStreamAction(false))
	}
}
