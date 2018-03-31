package se.gustavkarlsson.skylight.android.dagger.modules

import android.content.Context
import android.net.ConnectivityManager
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.hadisatrio.optional.Optional
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.Consumer
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.entities.AuroraFactors
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.extensions.singletonCache
import se.gustavkarlsson.skylight.android.services.DebugSettings
import se.gustavkarlsson.skylight.android.services.SingletonCache
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.*
import se.gustavkarlsson.skylight.android.services_impl.RxPreferencesDebugSettings
import se.gustavkarlsson.skylight.android.services_impl.providers.CombiningAuroraReportProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.DebugAuroraReportProvider
import se.gustavkarlsson.skylight.android.services_impl.streamables.CombiningAuroraReportStreamable
import se.gustavkarlsson.skylight.android.services_impl.streamables.DebugAuroraReportStreamable

@Module
class AuroraReportModule {

	@Provides
	@Reusable
	fun provideDebugSettings(
		rxSharedPreferences: RxSharedPreferences,
		context: Context
	): DebugSettings = RxPreferencesDebugSettings(context, rxSharedPreferences)

	@Provides
	@Reusable
	fun provideAuroraReportProvider(
		connectivityManager: ConnectivityManager,
		locationProvider: LocationProvider,
		auroraFactorsProvider: AuroraFactorsProvider,
		locationNameProvider: LocationNameProvider,
		timeProvider: TimeProvider,
		debugSettings: DebugSettings
	): AuroraReportProvider {
		val realProvider = CombiningAuroraReportProvider(
			connectivityManager,
			locationProvider,
			auroraFactorsProvider,
			locationNameProvider,
			timeProvider
		)
		return DebugAuroraReportProvider(realProvider, debugSettings, timeProvider)
	}

	@Provides
	@Reusable
	fun provideAuroraReportSingle(
		auroraReportProvider: AuroraReportProvider
	): Single<AuroraReport> {
		return auroraReportProvider.get()
	}

	@Provides
	@Reusable
	fun provideAuroraReportRelay(
	): Relay<AuroraReport> {
		return PublishRelay.create()
	}

	@Provides
	@Reusable
	fun provideAuroraReportConsumer(
		relay: Relay<AuroraReport>
	): Consumer<AuroraReport> {
		return relay
	}

	@Provides
	@Reusable
	fun provideAuroraReportStreamable(
		locationNames: Flowable<Optional<String>>,
		factors: Flowable<AuroraFactors>,
		now: Single<Instant>,
		debugSettings: DebugSettings,
		auroraReportRelay: Relay<AuroraReport>
	): Streamable<AuroraReport> {
		val realStreamable = CombiningAuroraReportStreamable(locationNames, factors, now,
			auroraReportRelay.toFlowable(BackpressureStrategy.LATEST))
		return DebugAuroraReportStreamable(realStreamable, debugSettings, now)
	}

	@Provides
	@Reusable
	fun provideAuroraReportFlowable(
		cache: SingletonCache<AuroraReport>,
		streamable: Streamable<AuroraReport>
	): Flowable<AuroraReport> = streamable.stream
		.singletonCache(cache)
		.replay(1)
		.refCount()
}
