package se.gustavkarlsson.skylight.android.di.modules

import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.DevelopSettings
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.services_impl.RxPreferencesDevelopSettings
import se.gustavkarlsson.skylight.android.services_impl.providers.CombiningAuroraReportProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.DevelopAuroraReportProvider
import se.gustavkarlsson.skylight.android.services_impl.streamables.CombiningAuroraReportStreamable
import se.gustavkarlsson.skylight.android.services_impl.streamables.DevelopAuroraReportStreamable

class DevelopAuroraReportModule(
	timeModule: TimeModule,
	locationModule: LocationModule,
	locationNameModule: LocationNameModule,
	darknessModule: DarknessModule,
	geomagLocationModule: GeomagLocationModule,
	kpIndexModule: KpIndexModule,
	weatherModule: WeatherModule,
	contextModule: ContextModule,
	rxSharedPreferencesModule: RxSharedPreferencesModule
) : AuroraReportModule {

	private val developSettings: DevelopSettings by lazy {
		RxPreferencesDevelopSettings(
			contextModule.context,
			rxSharedPreferencesModule.rxSharedPreferences
		)
	}

	override val auroraReportProvider: AuroraReportProvider by lazy {
		val realProvider = CombiningAuroraReportProvider(
			timeModule.timeProvider,
			locationModule.locationProvider,
			locationNameModule.locationNameProvider,
			darknessModule.darknessProvider,
			geomagLocationModule.geomagLocationProvider,
			kpIndexModule.kpIndexProvider,
			weatherModule.weatherProvider
		)
		DevelopAuroraReportProvider(realProvider, developSettings, timeModule.timeProvider)
	}

	private val auroraReportStreamable: Streamable<AuroraReport> by lazy {
		val realStreamable = CombiningAuroraReportStreamable(
			timeModule.now,
			locationNameModule.locationNameFlowable,
			kpIndexModule.kpIndexFlowable,
			geomagLocationModule.geomagLocationFlowable,
			darknessModule.darknessFlowable,
			weatherModule.weatherFlowable
		)
		DevelopAuroraReportStreamable(realStreamable, developSettings, timeModule.now)
	}

	override val auroraReportFlowable: Flowable<AuroraReport> by lazy {
		auroraReportStreamable.stream
			.replay(1)
			.refCount()
	}


}
