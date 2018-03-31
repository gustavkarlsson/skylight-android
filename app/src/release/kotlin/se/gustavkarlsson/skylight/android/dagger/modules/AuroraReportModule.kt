package se.gustavkarlsson.skylight.android.dagger.modules

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
import se.gustavkarlsson.skylight.android.services.SingletonCache
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.*
import se.gustavkarlsson.skylight.android.services_impl.providers.CombiningAuroraReportProvider
import se.gustavkarlsson.skylight.android.services_impl.streamables.CombiningAuroraReportStreamable

@Module
class AuroraReportModule {

	@Provides
	@Reusable
	fun provideAuroraReportProvider(
		locationProvider: LocationProvider,
		auroraFactorsProvider: AuroraFactorsProvider,
		locationNameProvider: LocationNameProvider,
		timeProvider: TimeProvider
	): AuroraReportProvider = CombiningAuroraReportProvider(
		locationProvider,
		auroraFactorsProvider,
		locationNameProvider,
		timeProvider
	)

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
		now: Single<Instant>
	): Streamable<AuroraReport> = CombiningAuroraReportStreamable(locationNames, factors, now)

	@Provides
	@Reusable
	fun provideAuroraReportFlowable(
		cache: SingletonCache<AuroraReport>,
		streamable: Streamable<AuroraReport>,
		relay: Relay<AuroraReport>
	): Flowable<AuroraReport> =
		Flowable.merge(streamable.stream, relay.toFlowable(BackpressureStrategy.LATEST))
			.singletonCache(cache)
			.replay(1)
			.refCount()
}
