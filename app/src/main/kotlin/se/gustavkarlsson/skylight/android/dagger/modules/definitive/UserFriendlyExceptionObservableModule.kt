package se.gustavkarlsson.skylight.android.dagger.modules.definitive

import dagger.Module
import dagger.Provides
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import javax.inject.Singleton

@Module
class UserFriendlyExceptionObservableModule {

	@Provides
	@Singleton
	fun provideUserFriendlyExceptionObservable(subject: Subject<UserFriendlyException>): Observable<UserFriendlyException> {
		return subject
	}

	@Provides
	@Singleton
	fun provideUserFriendlyExceptionSubject(): Subject<UserFriendlyException> {
		return PublishSubject.create<UserFriendlyException>()
	}
}
