package se.gustavkarlsson.skylight.android.di.modules

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import io.reactivex.Flowable
import io.reactivex.Single
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.gui.activities.main.MainViewModel
import se.gustavkarlsson.skylight.android.gui.activities.main.MainViewModelFactory
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance.AuroraChanceViewModel
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance.AuroraChanceViewModelFactory
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.*
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.formatters.RelativeTimeFormatter
import se.gustavkarlsson.skylight.android.services.formatters.SingleValueFormatter

class AndroidViewModelsModule(
	context: Context,
	auroraReportFlowable: Flowable<AuroraReport>,
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
	auroraReportSingle: Single<AuroraReport>,
	isConnectedToInternetFlowable: Flowable<Boolean>,
	now: Single<Instant>
) : ViewModelsModule {

	private val auroraChanceViewModelFactory: AuroraChanceViewModelFactory by lazy {
		AuroraChanceViewModelFactory(
			auroraReportFlowable,
			auroraChanceEvaluator,
			relativeTimeFormatter,
			chanceLevelFormatter,
			now,
			RIGHT_NOW_THRESHOLD
		)
	}

	private val auroraFactorsViewModelFactory: AuroraFactorsViewModelFactory by lazy {
		AuroraFactorsViewModelFactory(
			auroraReportFlowable,
			darknessChanceEvaluator,
			darknessFormatter,
			geomagLocationChanceEvaluator,
			geomagLocationFormatter,
			kpIndexChanceEvaluator,
			kpIndexFormatter,
			visibilityChanceEvaluator,
			visibilityFormatter
		)
	}


	private val mainViewModelFactory: MainViewModelFactory by lazy {
		MainViewModelFactory(
			auroraReportSingle,
			auroraReportFlowable,
			isConnectedToInternetFlowable,
			context.getString(R.string.your_location),
			context.getString(R.string.error_no_internet)
		)
	}
	override fun auroraChanceViewModel(fragment: Fragment): AuroraChanceViewModel =
		ViewModelProviders.of(fragment, auroraChanceViewModelFactory)
			.get(AuroraChanceViewModel::class.java)

	override fun darknessViewModel(fragment: Fragment): DarknessViewModel =
		ViewModelProviders.of(fragment, auroraFactorsViewModelFactory)
			.get(DarknessViewModel::class.java)

	override fun geomagLocationViewModel(fragment: Fragment): GeomagLocationViewModel =
		ViewModelProviders.of(fragment, auroraFactorsViewModelFactory)
			.get(GeomagLocationViewModel::class.java)

	override fun kpIndexViewModel(fragment: Fragment): KpIndexViewModel =
		ViewModelProviders.of(fragment, auroraFactorsViewModelFactory)
			.get(KpIndexViewModel::class.java)

	override fun visibilityViewModel(fragment: Fragment): VisibilityViewModel =
		ViewModelProviders.of(fragment, auroraFactorsViewModelFactory)
			.get(VisibilityViewModel::class.java)

	override fun mainViewModel(activity: FragmentActivity): MainViewModel =
		ViewModelProviders.of(activity, mainViewModelFactory)
			.get(MainViewModel::class.java)

	companion object {
		private val RIGHT_NOW_THRESHOLD = Duration.ofMinutes(1)
	}
}
