package se.gustavkarlsson.skylight.android.actions_impl.aurora_reports

import se.gustavkarlsson.skylight.android.actions.ShowLastAuroraReport
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.streams.StreamPublisher
import se.gustavkarlsson.skylight.android.util.UserFriendlyException

class ProvideLastAuroraReportToPublisher(
	provider: AuroraReportProvider,
	publisher: StreamPublisher<AuroraReport>,
	errorPublisher: StreamPublisher<UserFriendlyException>
) : ProvideAuroraReportToPublisher(provider, publisher, errorPublisher), ShowLastAuroraReport
