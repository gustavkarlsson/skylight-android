package se.gustavkarlsson.skylight.android.di.modules

import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.gui.activities.main.MainViewModel
import se.gustavkarlsson.skylight.android.gui.activities.main.MainViewModelFactory
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance.AuroraChanceViewModel
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance.AuroraChanceViewModelFactory
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.*

class AndroidViewModelsModule(
	contextModule: ContextModule,
	auroraReportModule: AuroraReportModule,
	evaluationModule: EvaluationModule,
	formattingModule: FormattingModule,
	connectivityModule: ConnectivityModule,
	timeModule: TimeModule
) : ViewModelsModule {

	private val auroraChanceViewModelFactory: AuroraChanceViewModelFactory by lazy {
		AuroraChanceViewModelFactory(
			auroraReportModule.auroraReportFlowable,
			evaluationModule.auroraReportEvaluator,
			formattingModule.relativeTimeFormatter,
			formattingModule.chanceLevelFormatter,
			timeModule.now,
			RIGHT_NOW_THRESHOLD
		)
	}

	private val auroraFactorsViewModelFactory: AuroraFactorsViewModelFactory by lazy {
		AuroraFactorsViewModelFactory(
			auroraReportModule.auroraReportFlowable,
			evaluationModule.darknessEvaluator,
			formattingModule.darknessFormatter,
			evaluationModule.geomagLocationEvaluator,
			formattingModule.geomagLocationFormatter,
			evaluationModule.kpIndexEvaluator,
			formattingModule.kpIndexFormatter,
			evaluationModule.visibilityEvaluator,
			formattingModule.visibilityFormatter
		)
	}


	private val mainViewModelFactory: MainViewModelFactory by lazy {
		MainViewModelFactory(
			auroraReportModule.auroraReportSingle,
			auroraReportModule.auroraReportFlowable,
			connectivityModule.connectivityFlowable,
			contextModule.context.getString(R.string.your_location),
			contextModule.context.getString(R.string.error_no_internet)
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
		private val RIGHT_NOW_THRESHOLD = 1.minutes
	}
}
