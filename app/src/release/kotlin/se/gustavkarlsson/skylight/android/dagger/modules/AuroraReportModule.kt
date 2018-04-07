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
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.*
import se.gustavkarlsson.skylight.android.services_impl.providers.CombiningAuroraReportProvider
import se.gustavkarlsson.skylight.android.services_impl.streamables.CombiningAuroraReportStreamable
import javax.inject.Singleton

@Module
class AuroraReportModule {

	@Provides
	@Reusable
	fun provideAuroraReportProvider(
		timeProvider: TimeProvider,
		locationProvider: LocationProvider,
		locationNameProvider: LocationNameProvider,
		darknessProvider: DarknessProvider,
		geomagLocationProvider: GeomagLocationProvider,
		kpIndexProvider: KpIndexProvider,
		visibilityProvider: VisibilityProvider
	): AuroraReportProvider = CombiningAuroraReportProvider(
		timeProvider,
		locationProvider,
		locationNameProvider,
		darknessProvider,
		geomagLocationProvider,
		kpIndexProvider,
		visibilityProvider
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
		now: Single<Instant>,
		locationNames: Flowable<Optional<String>>,
		kpIndexes: Flowable<KpIndex>,
		geomagLocations: Flowable<GeomagLocation>,
		darknesses: Flowable<Darkness>,
		visibilities: Flowable<Visibility>,
		relay: Relay<AuroraReport>
	): Streamable<AuroraReport> = CombiningAuroraReportStreamable(
		now,
		locationNames,
		kpIndexes,
		geomagLocations,
		darknesses,
		visibilities,
		relay.toFlowable(BackpressureStrategy.LATEST)
	)

	@Provides
	@Reusable
	fun provideAuroraReportFlowable(
		streamable: Streamable<AuroraReport>
	): Flowable<AuroraReport> = streamable.stream
		.startWith(AuroraReport.empty)
		.replay(1)
		.refCount()
}
