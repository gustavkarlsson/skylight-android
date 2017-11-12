package se.gustavkarlsson.skylight.android.dagger.modules

import android.content.Context
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.Relay
import dagger.Module
import dagger.Provides
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import se.gustavkarlsson.skylight.android.dagger.LAST_NAME
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.SingletonCache
import se.gustavkarlsson.skylight.android.services_impl.cache.DualSingletonCache
import se.gustavkarlsson.skylight.android.services_impl.cache.auroraReportCacheSerializer
import javax.inject.Named
import javax.inject.Singleton

@Module
class AuroraReportStreamModule {

	@Provides
	@Singleton
	fun provideLatestAuroraReportObservable(relay: Relay<AuroraReport>): Observable<AuroraReport> {
		return relay
	}

	@Provides
	@Singleton
	fun provideLatestAuroraReportConsumer(relay: Relay<AuroraReport>): Consumer<AuroraReport> {
		return relay
	}

	@Provides
	@Singleton
	fun provideLatestAuroraReportRelay(@Named(LAST_NAME) cache: SingletonCache<AuroraReport>): Relay<AuroraReport> {
		val relay = BehaviorRelay.createDefault(cache.value)
		relay.subscribe {
			cache.value = it
		}
		return relay
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
