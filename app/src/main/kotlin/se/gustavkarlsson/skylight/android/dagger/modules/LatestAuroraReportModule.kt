package se.gustavkarlsson.skylight.android.dagger.modules

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.LiveDataReactiveStreams
import android.content.Context
import dagger.Module
import dagger.Provides
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
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
	fun provideLatestAuroraReportLiveData(flowable: Flowable<AuroraReport>): LiveData<AuroraReport> {
		return LiveDataReactiveStreams.fromPublisher(flowable)
	}

	@Provides
	@Singleton
	fun provideLatestAuroraReportFlowable(observable: Observable<AuroraReport>): Flowable<AuroraReport> {
		return observable.toFlowable(BackpressureStrategy.LATEST)
	}

	@Provides
	@Singleton
	fun provideLatestAuroraReportObservable(subject: Subject<AuroraReport>): Observable<AuroraReport> {
		return subject
	}

	@Provides
	@Singleton
	fun provideLatestAuroraReportObserver(subject: Subject<AuroraReport>): Observer<AuroraReport> {
		return subject
	}

	@Provides
	@Singleton
	fun provideLatestAuroraReportSubject(@Named(LAST_NAME) cache: SingletonCache<AuroraReport>): Subject<AuroraReport> {
		return BehaviorSubject.createDefault(cache.value)
	}

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
