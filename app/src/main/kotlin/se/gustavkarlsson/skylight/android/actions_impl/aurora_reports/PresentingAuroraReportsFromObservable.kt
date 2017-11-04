package se.gustavkarlsson.skylight.android.actions_impl.aurora_reports

import dagger.Reusable
import io.reactivex.Observable
import se.gustavkarlsson.skylight.android.actions.PresentingAuroraReports
import se.gustavkarlsson.skylight.android.actions_impl.PresentingFromObservable
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.Presenter
import javax.inject.Inject

@Reusable
class PresentingAuroraReportsFromObservable
@Inject
constructor(
	observable: Observable<AuroraReport>,
	presenter: Presenter<AuroraReport>
) : PresentingFromObservable<AuroraReport>(observable, presenter), PresentingAuroraReports
