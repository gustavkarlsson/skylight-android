package se.gustavkarlsson.skylight.android.dagger.modules.replaceable

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.actions.ShowingErrors
import se.gustavkarlsson.skylight.android.actions.impl.ShowingErrorsFromStreamOnPresenter
import se.gustavkarlsson.skylight.android.services.Presenter
import se.gustavkarlsson.skylight.android.services.Stream
import se.gustavkarlsson.skylight.android.services.impl.ToastErrorPresenter
import se.gustavkarlsson.skylight.android.util.UserFriendlyException

@Module
class ShowingErrorsModule {

	@Provides
	@Reusable
	fun provideShowingErrors(errorStream: Stream<UserFriendlyException>, presenter: Presenter<UserFriendlyException>): ShowingErrors {
		return ShowingErrorsFromStreamOnPresenter(errorStream, presenter)
	}

	@Provides
	@Reusable
	fun provideUserFriendlyExceptionsPresenter(context: Context): Presenter<UserFriendlyException> {
		return ToastErrorPresenter(context)
	}
}
