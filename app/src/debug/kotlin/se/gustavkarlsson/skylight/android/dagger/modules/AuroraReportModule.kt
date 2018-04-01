package se.gustavkarlsson.skylight.android.dagger.modules

import android.content.Context
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
import se.gustavkarlsson.skylight.android.services.DebugSettings
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.*
import se.gustavkarlsson.skylight.android.services_impl.RxPreferencesDebugSettings
import se.gustavkarlsson.skylight.android.services_impl.providers.CombiningAuroraReportProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.DebugAuroraReportProvider
import se.gustavkarlsson.skylight.android.services_impl.streamables.CombiningAuroraReportStreamable
import se.gustavkarlsson.skylight.android.services_impl.streamables.DebugAuroraReportStreamable
import javax.inject.Singleton

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
		locationProvider: LocationProvider,
		auroraFactorsProvider: AuroraFactorsProvider,
		locationNameProvider: LocationNameProvider,
		timeProvider: TimeProvider,
		debugSettings: DebugSettings
	): AuroraReportProvider {
		val realProvider = CombiningAuroraReportProvider(
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
		auroraReportProvider: AuroraReportProvider,
		relay: Relay<AuroraReport>
	): Single<AuroraReport> {
		return auroraReportProvider.get()
			.doOnSuccess(relay)
	}

	@Provides
	@Singleton
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
		debugSettings: DebugSettings
	): Streamable<AuroraReport> {
		val realStreamable = CombiningAuroraReportStreamable(locationNames, factors, now)
		return DebugAuroraReportStreamable(realStreamable, debugSettings, now)
	}

	@Provides
	@Reusable
	fun provideAuroraReportFlowable(
		streamable: Streamable<AuroraReport>,
		relay: Relay<AuroraReport>
	): Flowable<AuroraReport> =
		Flowable.merge(streamable.stream, relay.toFlowable(BackpressureStrategy.LATEST))
			.startWith(AuroraReport.empty)
			.replay(1)
			.refCount()
}
