package se.gustavkarlsson.skylight.android.dagger.modules.replaceable

import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.actions.PresentingAuroraReports
import se.gustavkarlsson.skylight.android.actions_impl.errors.PresentingAuroraReportsFromStream
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance.TimeSinceUpdateController
import se.gustavkarlsson.skylight.android.services.Presenter
import se.gustavkarlsson.skylight.android.services.evaluation.Chance
import se.gustavkarlsson.skylight.android.services.evaluation.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.streams.Stream
import se.gustavkarlsson.skylight.android.services_impl.presenters.AuroraReportChancePresenter

@Module
class PresentingAuroraReportsModule {

	@Provides
	@Reusable
	fun providePresentingAuroraReports(errorStream: Stream<AuroraReport>, presenter: Presenter<AuroraReport>): PresentingAuroraReports {
		return PresentingAuroraReportsFromStream(errorStream, presenter)
	}

	@Provides
	@Reusable
	fun provideAuroraReportsPresenter(auroraChanceEvaluator: ChanceEvaluator<AuroraReport>, locationPresenter: Presenter<String?>, chancePresenter: Presenter<Chance>, timeSinceUpdateController: TimeSinceUpdateController): Presenter<AuroraReport> {
		return AuroraReportChancePresenter(auroraChanceEvaluator, locationPresenter, chancePresenter, timeSinceUpdateController)
	}
}
