package se.gustavkarlsson.skylight.android.services_impl.presenters

import dagger.Reusable
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance.TimeSinceUpdateController
import se.gustavkarlsson.skylight.android.services.Presenter
import se.gustavkarlsson.skylight.android.services.evaluation.Chance
import se.gustavkarlsson.skylight.android.services.evaluation.ChanceEvaluator
import javax.inject.Inject

@Reusable
class AuroraReportChanceFragmentPresenter
@Inject
constructor(
        private val auroraChanceEvaluator: ChanceEvaluator<AuroraReport>,
        private val locationPresenter: Presenter<String?>,
        private val chancePresenter: Presenter<Chance>,
        private val timeSinceUpdateController: TimeSinceUpdateController
) : Presenter<AuroraReport> {

    override fun present(value: AuroraReport) {
        locationPresenter.present(value.locationName)
        chancePresenter.present(auroraChanceEvaluator.evaluate(value))
        timeSinceUpdateController.update(value.timestamp)
    }
}
