package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Binds
import dagger.Module
import dagger.Reusable
import se.gustavkarlsson.skylight.android.actions.PresentingErrors
import se.gustavkarlsson.skylight.android.actions_impl.errors.PresentingErrorsFromObservable
import se.gustavkarlsson.skylight.android.services.Presenter
import se.gustavkarlsson.skylight.android.services_impl.presenters.ErrorToastPresenter
import se.gustavkarlsson.skylight.android.util.UserFriendlyException

@Module
abstract class PresentingErrorsModule {

	@Binds
	@Reusable
	abstract fun bindPresentingErrors(impl: PresentingErrorsFromObservable): PresentingErrors

	@Binds
	@Reusable
	abstract fun bindErrorToastPresenter(impl: ErrorToastPresenter): Presenter<UserFriendlyException>
}
