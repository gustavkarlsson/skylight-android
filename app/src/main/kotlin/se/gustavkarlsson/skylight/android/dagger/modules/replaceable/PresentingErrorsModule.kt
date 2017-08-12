package se.gustavkarlsson.skylight.android.dagger.modules.replaceable

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.actions.PresentingErrors
import se.gustavkarlsson.skylight.android.actions_impl.errors.PresentingErrorsFromStream
import se.gustavkarlsson.skylight.android.services.Presenter
import se.gustavkarlsson.skylight.android.services.streams.Stream
import se.gustavkarlsson.skylight.android.services_impl.presenters.ErrorToastPresenter
import se.gustavkarlsson.skylight.android.util.UserFriendlyException

@Module
class PresentingErrorsModule {

	@Provides
	@Reusable
	fun providePresentingErrors(errorStream: Stream<UserFriendlyException>, presenter: Presenter<UserFriendlyException>): PresentingErrors {
		return PresentingErrorsFromStream(errorStream, presenter)
	}

	@Provides
	@Reusable
	fun provideUserFriendlyExceptionsPresenter(context: Context): Presenter<UserFriendlyException> {
		return ErrorToastPresenter(context)
	}
}
