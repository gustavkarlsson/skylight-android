package se.gustavkarlsson.skylight.android.actions_impl.aurora_reports

import se.gustavkarlsson.skylight.android.actions.ShowNewAuroraReport
import se.gustavkarlsson.skylight.android.actions_impl.ProvideToPublisher
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.Provider
import se.gustavkarlsson.skylight.android.services.streams.StreamPublisher
import se.gustavkarlsson.skylight.android.util.UserFriendlyException

class ProvideNewAuroraReportToPublisher(
	provider: Provider<AuroraReport>,
	publisher: StreamPublisher<AuroraReport>,
	errorPublisher: StreamPublisher<UserFriendlyException>
) : ProvideToPublisher<AuroraReport>(provider, publisher, errorPublisher), ShowNewAuroraReport
