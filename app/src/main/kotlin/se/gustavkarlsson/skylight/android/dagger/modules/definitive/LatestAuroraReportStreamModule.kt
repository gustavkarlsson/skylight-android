package se.gustavkarlsson.skylight.android.dagger.modules.definitive

import dagger.Module
import dagger.Provides
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import se.gustavkarlsson.skylight.android.dagger.LAST_NAME
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.SingletonCache
import se.gustavkarlsson.skylight.android.services.streams.Stream
import se.gustavkarlsson.skylight.android.services.streams.StreamPublisher
import se.gustavkarlsson.skylight.android.services_impl.streams.RxStream
import se.gustavkarlsson.skylight.android.services_impl.streams.RxStreamPublisher
import javax.inject.Named
import javax.inject.Singleton

@Module
class LatestAuroraReportStreamModule {

	@Provides
	@Singleton
	fun provideLatestAuroraReportStream(subject: Subject<AuroraReport>): Stream<AuroraReport> {
		return RxStream(subject)
	}

	// TODO Get rid of this
	@Provides
	@Singleton
	fun provideLatestAuroraReportObservable(subject: Subject<AuroraReport>): Observable<AuroraReport> {
		return subject
	}

	@Provides
	@Singleton
	fun provideLatestAuroraReportStreamPublisher(subject: Subject<AuroraReport>): StreamPublisher<AuroraReport> {
		return RxStreamPublisher(subject)
	}

	@Provides
	@Singleton
	fun provideLatestAuroraReportSubject(@Named(LAST_NAME) cache: SingletonCache<AuroraReport>): Subject<AuroraReport> {
		val subject = BehaviorSubject.createDefault(cache.value)
		subject.doOnNext { cache.value = it }
		return subject
	}
}
