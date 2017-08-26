package se.gustavkarlsson.skylight.android.actions_impl.aurora_reports

import dagger.Reusable
import se.gustavkarlsson.skylight.android.actions.PresentingAuroraReports
import se.gustavkarlsson.skylight.android.actions_impl.PresentingFromStream
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.Presenter
import se.gustavkarlsson.skylight.android.services.streams.Stream
import javax.inject.Inject

@Reusable
class PresentingAuroraReportsFromStream
@Inject
constructor(
	stream: Stream<AuroraReport>,
	presenter: Presenter<AuroraReport>
) : PresentingFromStream<AuroraReport>(stream, presenter), PresentingAuroraReports
