package se.gustavkarlsson.skylight.android.dagger.modules.definitive

import dagger.Module
import dagger.Provides
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import se.gustavkarlsson.skylight.android.services.Stream
import se.gustavkarlsson.skylight.android.services.StreamPublisher
import se.gustavkarlsson.skylight.android.services.impl.RxStream
import se.gustavkarlsson.skylight.android.services.impl.RxStreamPublisher
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import javax.inject.Singleton

@Module
class UserFriendlyExceptionStreamModule {

	@Provides
	@Singleton
	fun provideUserFriendlyExceptionStream(subject: Subject<UserFriendlyException>): Stream<UserFriendlyException> {
		return RxStream(subject)
	}

	@Provides
	@Singleton
	fun provideUserFriendlyExceptionStreamPublisher(subject: Subject<UserFriendlyException>): StreamPublisher<UserFriendlyException> {
		return RxStreamPublisher(subject)
	}

	@Provides
	@Singleton
	fun provideUserFriendlyExceptionSubject(): Subject<UserFriendlyException> {
		return PublishSubject.create<UserFriendlyException>()
	}
}
