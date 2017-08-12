package se.gustavkarlsson.skylight.android.services_impl.presenters

import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance.TimeSinceUpdateController
import se.gustavkarlsson.skylight.android.services.Presenter
import se.gustavkarlsson.skylight.android.services.evaluation.Chance
import se.gustavkarlsson.skylight.android.services.evaluation.ChanceEvaluator

class AuroraReportChancePresenter(
	val auroraChanceEvaluator: ChanceEvaluator<AuroraReport>,
	val locationPresenter: Presenter<String?>,
	val chancePresenter: Presenter<Chance>,
	val timeSinceUpdateController: TimeSinceUpdateController
) : Presenter<AuroraReport> {

	override fun present(value: AuroraReport) {
		locationPresenter.present(value.location)
		chancePresenter.present(auroraChanceEvaluator.evaluate(value))
		timeSinceUpdateController.update(value.timestamp)
	}
}
