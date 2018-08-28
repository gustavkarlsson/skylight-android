package se.gustavkarlsson.skylight.android.di.modules

import android.support.v4.app.Fragment
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.extensions.getViewModel
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.gui.screens.main.MainViewModel
import se.gustavkarlsson.skylight.android.gui.screens.main.MainViewModelFactory
import se.gustavkarlsson.skylight.android.gui.screens.setup.SetupViewModel
import se.gustavkarlsson.skylight.android.gui.screens.setup.SetupViewModelFactory

class AndroidViewModelsModule(
	krateModule: KrateModule,
	contextModule: ContextModule,
	evaluationModule: EvaluationModule,
	formattingModule: FormattingModule,
	timeModule: TimeModule,
	rightNowThreshold: Duration = 1.minutes
) : ViewModelsModule {

	private val mainViewModelFactory by lazy {
		MainViewModelFactory(
			krateModule.store,
			contextModule.context.getString(R.string.main_your_location),
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
			evaluationModule.weatherEvaluator,
			formattingModule.weatherFormatter,
			timeModule.now,
			rightNowThreshold
		)
	}

	private val setupViewModelFactory by lazy {
		SetupViewModelFactory(krateModule.store)
	}

	override fun mainViewModel(fragment: Fragment): MainViewModel =
		fragment.getViewModel(mainViewModelFactory)

	override fun setupViewModel(fragment: Fragment): SetupViewModel =
		fragment.getViewModel(setupViewModelFactory)
}
