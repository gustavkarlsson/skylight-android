package se.gustavkarlsson.skylight.android.di.modules

import android.arch.lifecycle.ViewModelProviders
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

	override fun auroraChanceViewModel(activity: FragmentActivity): AuroraChanceViewModel =
		ViewModelProviders.of(activity, auroraChanceViewModelFactory)
			.get(AuroraChanceViewModel::class.java)

	override fun darknessViewModel(activity: FragmentActivity): DarknessViewModel =
		ViewModelProviders.of(activity, auroraFactorsViewModelFactory)
			.get(DarknessViewModel::class.java)

	override fun geomagLocationViewModel(activity: FragmentActivity): GeomagLocationViewModel =
		ViewModelProviders.of(activity, auroraFactorsViewModelFactory)
			.get(GeomagLocationViewModel::class.java)

	override fun kpIndexViewModel(activity: FragmentActivity): KpIndexViewModel =
		ViewModelProviders.of(activity, auroraFactorsViewModelFactory)
			.get(KpIndexViewModel::class.java)

	override fun visibilityViewModel(activity: FragmentActivity): VisibilityViewModel =
		ViewModelProviders.of(activity, auroraFactorsViewModelFactory)
			.get(VisibilityViewModel::class.java)

	override fun mainViewModel(activity: FragmentActivity): MainViewModel =
		ViewModelProviders.of(activity, mainViewModelFactory)
			.get(MainViewModel::class.java)

	companion object {
		private val RIGHT_NOW_THRESHOLD = 1.minutes
	}
}
