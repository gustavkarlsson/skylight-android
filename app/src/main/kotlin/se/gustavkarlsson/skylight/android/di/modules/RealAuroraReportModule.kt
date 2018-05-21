package se.gustavkarlsson.skylight.android.di.modules

import io.reactivex.Flowable
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.CombiningAuroraReportProvider
import se.gustavkarlsson.skylight.android.services_impl.streamables.CombiningAuroraReportStreamable

class RealAuroraReportModule(
	timeModule: TimeModule,
	locationModule: LocationModule,
	locationNameModule: LocationNameModule,
	darknessModule: DarknessModule,
	geomagLocationModule: GeomagLocationModule,
	kpIndexModule: KpIndexModule,
	visibilityModule: VisibilityModule
) : AuroraReportModule {

	override val auroraReportProvider: AuroraReportProvider by lazy {
		CombiningAuroraReportProvider(
			timeModule.timeProvider,
			locationModule.locationProvider,
			locationNameModule.locationNameProvider,
			darknessModule.darknessProvider,
			geomagLocationModule.geomagLocationProvider,
			kpIndexModule.kpIndexProvider,
			visibilityModule.visibilityProvider
		)
	}

	override val auroraReportSingle: Single<AuroraReport> by lazy {
		auroraReportProvider.get()
	}

	override val auroraReportStreamable: Streamable<AuroraReport> by lazy {
		CombiningAuroraReportStreamable(
			timeModule.now,
			locationNameModule.locationNameFlowable,
			kpIndexModule.kpIndexFlowable,
			geomagLocationModule.geomagLocationFlowable,
			darknessModule.darknessFlowable,
			visibilityModule.visibilityFlowable
		)
	}

	override val auroraReportFlowable: Flowable<AuroraReport> by lazy {
		auroraReportStreamable.stream
			.startWith(AuroraReport.empty)
			.replay(1)
			.refCount()
	}
}
