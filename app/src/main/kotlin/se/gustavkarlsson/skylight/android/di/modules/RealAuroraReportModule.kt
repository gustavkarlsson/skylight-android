package se.gustavkarlsson.skylight.android.di.modules

import com.hadisatrio.optional.Optional
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.Consumer
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.*
import se.gustavkarlsson.skylight.android.services_impl.providers.CombiningAuroraReportProvider
import se.gustavkarlsson.skylight.android.services_impl.streamables.CombiningAuroraReportStreamable

class RealAuroraReportModule(
	timeProvider: TimeProvider,
	locationProvider: LocationProvider,
	locationNameProvider: LocationNameProvider,
	darknessProvider: DarknessProvider,
	geomagLocationProvider: GeomagLocationProvider,
	kpIndexProvider: KpIndexProvider,
	visibilityProvider: VisibilityProvider,
	locationNames: Flowable<Optional<String>>,
	kpIndexes: Flowable<KpIndex>,
	geomagLocations: Flowable<GeomagLocation>,
	darknesses: Flowable<Darkness>,
	visibilities: Flowable<Visibility>
) : AuroraReportModule {

	private val auroraReportRelay: Relay<AuroraReport> by lazy {
		PublishRelay.create<AuroraReport>()
	}

	override val auroraReportProvider: AuroraReportProvider by lazy {
		CombiningAuroraReportProvider(
			timeProvider,
			locationProvider,
			locationNameProvider,
			darknessProvider,
			geomagLocationProvider,
			kpIndexProvider,
			visibilityProvider
		)
	}

	override val auroraReportSingle: Single<AuroraReport> by lazy {
		auroraReportProvider.get()
			.doOnSuccess(auroraReportRelay)
	}

	override val provideAuroraReportConsumer: Consumer<AuroraReport> by lazy {
		auroraReportRelay
	}

	override val auroraReportStreamable: Streamable<AuroraReport> by lazy {
		CombiningAuroraReportStreamable(
			timeProvider.getTime(),
			locationNames,
			kpIndexes,
			geomagLocations,
			darknesses,
			visibilities,
			auroraReportRelay.toFlowable(BackpressureStrategy.LATEST)
		)
	}

	override val auroraReportFlowable: Flowable<AuroraReport> by lazy {
		auroraReportStreamable.stream
			.startWith(AuroraReport.empty)
			.replay(1)
			.refCount()
	}
}
