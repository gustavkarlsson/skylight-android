package se.gustavkarlsson.skylight.android.dagger.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import se.gustavkarlsson.skylight.android.dagger.LAST_NAME
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.SingletonCache
import se.gustavkarlsson.skylight.android.services_impl.cache.DualSingletonCache
import se.gustavkarlsson.skylight.android.services_impl.cache.auroraReportCacheSerializer
import javax.inject.Named
import javax.inject.Singleton

@Module
class LatestAuroraReportModule {

	@Provides
	@Singleton
	fun provideLatestAuroraReportObservable(subject: Subject<AuroraReport>): Observable<AuroraReport> = subject

	@Provides
	@Singleton
	fun provideLatestAuroraReportObserver(subject: Subject<AuroraReport>): Observer<AuroraReport> = subject

	@Provides
	@Singleton
	fun provideLatestAuroraReportSubject(@Named(LAST_NAME) cache: SingletonCache<AuroraReport>): Subject<AuroraReport> = BehaviorSubject.createDefault(cache.value)

	@Provides
	@Singleton
	@Named(LAST_NAME)
	fun provideLatestAuroraReportCache(context: Context): SingletonCache<AuroraReport> {
		return DualSingletonCache(CACHE_ID, AuroraReport.empty, auroraReportCacheSerializer, context)
	}

	companion object {
		const val CACHE_ID = "latest-aurora-report"
	}
}
