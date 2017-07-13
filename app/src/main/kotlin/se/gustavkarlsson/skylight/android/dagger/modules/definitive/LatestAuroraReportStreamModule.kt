package se.gustavkarlsson.skylight.android.dagger.modules.definitive

import dagger.Module
import dagger.Provides
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import org.threeten.bp.Clock
import se.gustavkarlsson.skylight.android.cache.SingletonCache
import se.gustavkarlsson.skylight.android.dagger.LATEST_NAME
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.extensions.now
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
	fun provideLatestAuroraReportSubject(@Named(LATEST_NAME) cache: SingletonCache<AuroraReport>, clock: Clock): Subject<AuroraReport> {
		var report = cache.value
		if (report == null) {
			val factors = AuroraFactors(
				GeomagActivity(null),
				GeomagLocation(null),
				Darkness(null),
				Visibility(null)
			)
			report = AuroraReport(clock.now, null, factors)
		}
		val subject = BehaviorSubject.createDefault(report)
		subject.doOnNext { cache.value = it }
		return subject
	}
}
