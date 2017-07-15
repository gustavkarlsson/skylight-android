package se.gustavkarlsson.skylight.android.actions.impl

import se.gustavkarlsson.skylight.android.actions.ShowingErrors
import se.gustavkarlsson.skylight.android.services.Presenter
import se.gustavkarlsson.skylight.android.services.Stream
import se.gustavkarlsson.skylight.android.util.UserFriendlyException

class ShowingErrorsFromStreamOnPresenter(
	stream: Stream<UserFriendlyException>,
	presenter: Presenter<UserFriendlyException>
) : ShowingFromStreamOnPresenter<UserFriendlyException>(stream, presenter), ShowingErrors
