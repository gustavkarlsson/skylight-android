package se.gustavkarlsson.skylight.android.actions_impl.errors

import dagger.Reusable
import io.reactivex.Observable
import se.gustavkarlsson.skylight.android.actions.PresentingErrors
import se.gustavkarlsson.skylight.android.actions_impl.PresentingFromObservable
import se.gustavkarlsson.skylight.android.services.Presenter
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import javax.inject.Inject

@Reusable
class PresentingErrorsFromObservable
@Inject
constructor(
	observable: Observable<UserFriendlyException>,
	presenter: Presenter<UserFriendlyException>
) : PresentingFromObservable<UserFriendlyException>(observable, presenter), PresentingErrors
