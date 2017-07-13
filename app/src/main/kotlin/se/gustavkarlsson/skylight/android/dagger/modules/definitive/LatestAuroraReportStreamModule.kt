package se.gustavkarlsson.skylight.android.dagger.modules.definitive

import dagger.Module
import dagger.Provides
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import se.gustavkarlsson.skylight.android.dagger.LATEST_NAME
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.SingletonCache
import se.gustavkarlsson.skylight.android.services.Stream
import se.gustavkarlsson.skylight.android.services.StreamPublisher
import se.gustavkarlsson.skylight.android.services.impl.RxStream
import se.gustavkarlsson.skylight.android.services.impl.RxStreamPublisher
import javax.inject.Named
import javax.inject.Singleton

@Module
class LatestAuroraReportStreamModule {

	@Provides
	@Singleton
	@Named(LATEST_NAME)
	fun provideLatestAuroraReportStream(@Named(LATEST_NAME) subject: Subject<AuroraReport>): Stream<AuroraReport> {
		return RxStream(subject)
	}

	// TODO Get rid of this
	@Provides
	@Singleton
	@Named(LATEST_NAME)
	fun provideLatestAuroraReportObservable(@Named(LATEST_NAME) subject: Subject<AuroraReport>): Observable<AuroraReport> {
		return subject
	}

	@Provides
	@Singleton
	@Named(LATEST_NAME)
	fun provideLatestAuroraReportStreamPublisher(@Named(LATEST_NAME) subject: Subject<AuroraReport>): StreamPublisher<AuroraReport> {
		return RxStreamPublisher(subject)
	}

	@Provides
	@Singleton
	@Named(LATEST_NAME)
	fun provideLatestAuroraReportSubject(@Named(LATEST_NAME) cache: SingletonCache<AuroraReport>): Subject<AuroraReport> {
		val subject = BehaviorSubject.createDefault(cache.value)
		subject.doOnNext { cache.value = it }
		return subject
	}
}
