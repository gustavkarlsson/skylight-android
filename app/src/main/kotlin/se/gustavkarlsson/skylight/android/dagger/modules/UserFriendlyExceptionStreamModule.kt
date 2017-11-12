package se.gustavkarlsson.skylight.android.dagger.modules

import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import dagger.Module
import dagger.Provides
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import javax.inject.Singleton

@Module
class UserFriendlyExceptionStreamModule {

	@Provides
	@Singleton
	fun provideUserFriendlyExceptionObservable(relay: Relay<UserFriendlyException>): Observable<UserFriendlyException> = relay

	@Provides
	@Singleton
	fun provideUserFriendlyExceptionConsumer(relay: Relay<UserFriendlyException>): Consumer<UserFriendlyException> = relay

	@Provides
	@Singleton
	fun provideUserFriendlyExceptionRelay(): Relay<UserFriendlyException> = PublishRelay.create<UserFriendlyException>()
}
