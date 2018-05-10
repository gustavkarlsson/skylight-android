package se.gustavkarlsson.skylight.android.di.modules

import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentActivity
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.gui.activities.main.MainViewModel
import se.gustavkarlsson.skylight.android.gui.activities.main.MainViewModelFactory

class AndroidViewModelsModule(
	contextModule: ContextModule,
	auroraReportModule: AuroraReportModule,
	evaluationModule: EvaluationModule,
	formattingModule: FormattingModule,
	connectivityModule: ConnectivityModule,
	timeModule: TimeModule
) : ViewModelsModule {


	private val mainViewModelFactory: MainViewModelFactory by lazy {
		MainViewModelFactory(
			auroraReportModule.auroraReportSingle,
			auroraReportModule.auroraReportFlowable,
			connectivityModule.connectivityFlowable,
			contextModule.context.getString(R.string.your_location),
			contextModule.context.getString(R.string.error_no_internet),
			evaluationModule.auroraReportEvaluator,
			formattingModule.relativeTimeFormatter,
			formattingModule.chanceLevelFormatter,
			evaluationModule.darknessEvaluator,
			formattingModule.darknessFormatter,
			evaluationModule.geomagLocationEvaluator,
			formattingModule.geomagLocationFormatter,
			evaluationModule.kpIndexEvaluator,
			formattingModule.kpIndexFormatter,
			evaluationModule.visibilityEvaluator,
			formattingModule.visibilityFormatter,
			timeModule.now,
			RIGHT_NOW_THRESHOLD
		)
	}

	override fun mainViewModel(activity: FragmentActivity): MainViewModel =
		ViewModelProviders.of(activity, mainViewModelFactory)
			.get(MainViewModel::class.java)

	companion object {
		private val RIGHT_NOW_THRESHOLD = 1.minutes
	}
}
