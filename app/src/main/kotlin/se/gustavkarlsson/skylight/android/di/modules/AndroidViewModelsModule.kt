package se.gustavkarlsson.skylight.android.di.modules

import android.content.Context
import io.reactivex.Flowable
import io.reactivex.Single
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.gui.activities.main.MainViewModelFactory
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance.AuroraChanceViewModelFactory
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.AuroraFactorsViewModelFactory
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

	override val auroraChanceViewModelFactory: AuroraChanceViewModelFactory by lazy {
		AuroraChanceViewModelFactory(
			auroraReportFlowable,
			auroraChanceEvaluator,
			relativeTimeFormatter,
			chanceLevelFormatter,
			now,
			RIGHT_NOW_THRESHOLD
		)
	}

	override val auroraFactorsViewModelFactory: AuroraFactorsViewModelFactory by lazy {
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


	override val mainViewModelFactory: MainViewModelFactory by lazy {
		MainViewModelFactory(
			auroraReportSingle,
			auroraReportFlowable,
			isConnectedToInternetFlowable,
			context.getString(R.string.your_location),
			context.getString(R.string.error_no_internet)
		)
	}

	companion object {
		private val RIGHT_NOW_THRESHOLD = Duration.ofMinutes(1)
	}
}
