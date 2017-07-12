package se.gustavkarlsson.skylight.android.dagger.modules.definitive

import dagger.Module
import dagger.Provides
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import org.threeten.bp.Clock
import se.gustavkarlsson.skylight.android.cache.SingletonCache
import se.gustavkarlsson.skylight.android.dagger.LATEST_NAME
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.extensions.now
import javax.inject.Named
import javax.inject.Singleton

@Module
class LatestAuroraReportObservableModule {

	@Provides
	@Singleton
	@Named(LATEST_NAME)
	fun provideLatestAuroraReportObservable(@Named(LATEST_NAME) subject: Subject<AuroraReport>): Observable<AuroraReport> {
		return subject
	}

	@Provides
	@Singleton
	@Named(LATEST_NAME)
	fun provideLatestAuroraReportObserver(@Named(LATEST_NAME) subject: Subject<AuroraReport>): Observer<AuroraReport> {
		return subject
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
