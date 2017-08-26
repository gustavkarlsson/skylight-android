package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Module
import dagger.Provides
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import se.gustavkarlsson.skylight.android.services.streams.Stream
import se.gustavkarlsson.skylight.android.services.streams.StreamPublisher
import se.gustavkarlsson.skylight.android.services_impl.streams.RxStream
import se.gustavkarlsson.skylight.android.services_impl.streams.RxStreamPublisher
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import javax.inject.Singleton

@Module
class UserFriendlyExceptionStreamModule {

    @Provides
    @Singleton
    fun provideUserFriendlyExceptionStream(subject: Subject<UserFriendlyException>): Stream<UserFriendlyException> = RxStream(subject)

    @Provides
    @Singleton
    fun provideUserFriendlyExceptionStreamPublisher(subject: Subject<UserFriendlyException>): StreamPublisher<UserFriendlyException> = RxStreamPublisher(subject)

    @Provides
    @Singleton
    fun provideUserFriendlyExceptionSubject(): Subject<UserFriendlyException> = PublishSubject.create<UserFriendlyException>()
}
