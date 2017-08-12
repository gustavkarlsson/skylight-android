package se.gustavkarlsson.skylight.android.actions_impl.errors

import se.gustavkarlsson.skylight.android.actions.PresentingAuroraReports
import se.gustavkarlsson.skylight.android.actions_impl.PresentingFromStream
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.Presenter
import se.gustavkarlsson.skylight.android.services.streams.Stream

class PresentingAuroraReportsFromStream(
	stream: Stream<AuroraReport>,
	presenter: Presenter<AuroraReport>
) : PresentingFromStream<AuroraReport>(stream, presenter), PresentingAuroraReports
