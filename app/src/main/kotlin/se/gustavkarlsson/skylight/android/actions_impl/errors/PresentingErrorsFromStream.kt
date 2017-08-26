package se.gustavkarlsson.skylight.android.actions_impl.errors

import dagger.Reusable
import se.gustavkarlsson.skylight.android.actions.PresentingErrors
import se.gustavkarlsson.skylight.android.actions_impl.PresentingFromStream
import se.gustavkarlsson.skylight.android.services.Presenter
import se.gustavkarlsson.skylight.android.services.streams.Stream
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import javax.inject.Inject

@Reusable
class PresentingErrorsFromStream
@Inject
constructor(
	stream: Stream<UserFriendlyException>,
	presenter: Presenter<UserFriendlyException>
) : PresentingFromStream<UserFriendlyException>(stream, presenter), PresentingErrors
