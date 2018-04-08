package se.gustavkarlsson.skylight.android.di.modules

import android.content.Context
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.hadisatrio.optional.Optional
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.Consumer
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.services.DebugSettings
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.*
import se.gustavkarlsson.skylight.android.services_impl.RxPreferencesDebugSettings
import se.gustavkarlsson.skylight.android.services_impl.providers.CombiningAuroraReportProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.DebugAuroraReportProvider
import se.gustavkarlsson.skylight.android.services_impl.streamables.CombiningAuroraReportStreamable
import se.gustavkarlsson.skylight.android.services_impl.streamables.DebugAuroraReportStreamable

class DebugAuroraReportModule(
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
	visibilities: Flowable<Visibility>,
	context: Context,
	rxSharedPreferences: RxSharedPreferences
) : AuroraReportModule {

	private val debugSettings: DebugSettings by lazy {
		RxPreferencesDebugSettings(context, rxSharedPreferences)

	}

	private val auroraReportRelay: Relay<AuroraReport> by lazy {
		PublishRelay.create<AuroraReport>()
	}

	override val auroraReportProvider: AuroraReportProvider by lazy {
		val realProvider = CombiningAuroraReportProvider(
			timeProvider,
			locationProvider,
			locationNameProvider,
			darknessProvider,
			geomagLocationProvider,
			kpIndexProvider,
			visibilityProvider
		)
		DebugAuroraReportProvider(realProvider, debugSettings, timeProvider)
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
			timeProvider.getTime(),
			locationNames,
			kpIndexes,
			geomagLocations,
			darknesses,
			visibilities,
			auroraReportRelay.toFlowable(BackpressureStrategy.LATEST)
		)
		DebugAuroraReportStreamable(realStreamable, debugSettings, timeProvider.getTime())
	}

	override val auroraReportFlowable: Flowable<AuroraReport> by lazy {
		auroraReportStreamable.stream
			.startWith(AuroraReport.empty)
			.replay(1)
			.refCount()
	}


}
