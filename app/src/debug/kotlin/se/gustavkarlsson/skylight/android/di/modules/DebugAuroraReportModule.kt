package se.gustavkarlsson.skylight.android.di.modules

import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.Consumer
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.DebugSettings
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.services_impl.RxPreferencesDebugSettings
import se.gustavkarlsson.skylight.android.services_impl.providers.CombiningAuroraReportProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.DebugAuroraReportProvider
import se.gustavkarlsson.skylight.android.services_impl.streamables.CombiningAuroraReportStreamable
import se.gustavkarlsson.skylight.android.services_impl.streamables.DebugAuroraReportStreamable

class DebugAuroraReportModule(
	timeModule: TimeModule,
	locationModule: LocationModule,
	locationNameModule: LocationNameModule,
	darknessModule: DarknessModule,
	geomagLocationModule: GeomagLocationModule,
	kpIndexModule: KpIndexModule,
	visibilityModule: VisibilityModule,
	contextModule: ContextModule,
	settingsModule: SettingsModule
) : AuroraReportModule {

	private val debugSettings: DebugSettings by lazy {
		RxPreferencesDebugSettings(contextModule.context, settingsModule.rxSharedPreferences)
	}

	private val auroraReportRelay: Relay<AuroraReport> by lazy {
		PublishRelay.create<AuroraReport>()
	}

	override val auroraReportProvider: AuroraReportProvider by lazy {
		val realProvider = CombiningAuroraReportProvider(
			timeModule.timeProvider,
			locationModule.locationProvider,
			locationNameModule.locationNameProvider,
			darknessModule.darknessProvider,
			geomagLocationModule.geomagLocationProvider,
			kpIndexModule.kpIndexProvider,
			visibilityModule.visibilityProvider
		)
		DebugAuroraReportProvider(realProvider, debugSettings, timeModule.timeProvider)
	}

	override val auroraReportSingle: Single<AuroraReport> by lazy {
		auroraReportProvider.get()
			.doOnSuccess(auroraReportRelay)
	}
	override val provideAuroraReportConsumer: Consumer<AuroraReport> by lazy {
		auroraReportRelay
	}

	override val auroraReportStreamable: Streamable<AuroraReport> by lazy {
		val realStreamable = CombiningAuroraReportStreamable(
			timeModule.now,
			locationNameModule.locationNameFlowable,
			kpIndexModule.kpIndexFlowable,
			geomagLocationModule.geomagLocationFlowable,
			darknessModule.darknessFlowable,
			visibilityModule.visibilityFlowable,
			auroraReportRelay.toFlowable(BackpressureStrategy.LATEST)
		)
		DebugAuroraReportStreamable(realStreamable, debugSettings, timeModule.now)
	}

	override val auroraReportFlowable: Flowable<AuroraReport> by lazy {
		auroraReportStreamable.stream
			.startWith(AuroraReport.empty)
			.replay(1)
			.refCount()
	}


}
