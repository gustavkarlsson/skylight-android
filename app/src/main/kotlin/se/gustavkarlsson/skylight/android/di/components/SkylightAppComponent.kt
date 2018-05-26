package se.gustavkarlsson.skylight.android.di.components

import android.app.Application
import android.support.v4.app.FragmentActivity
import se.gustavkarlsson.skylight.android.background.di.components.BackgroundComponent
import se.gustavkarlsson.skylight.android.background.di.components.SkylightBackgroundComponent
import se.gustavkarlsson.skylight.android.di.modules.*
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.flux.SkylightStore
import se.gustavkarlsson.skylight.android.gui.activities.main.MainActivity
import se.gustavkarlsson.skylight.android.gui.activities.main.MainViewModel
import se.gustavkarlsson.skylight.android.services.Analytics
import se.gustavkarlsson.skylight.android.services.Settings

open class SkylightAppComponent(
	openWeatherMapApiKey: String, // TODO Is this really the right way to do this?
	application: Application
) : AppComponent {

	open val contextModule: ContextModule by lazy {
		ApplicationContextModule(application)
	}

	open val timeModule: TimeModule by lazy {
		SystemTimeModule()
	}

	open val locationModule: LocationModule by lazy {
		ReactiveLocationModule(contextModule)
	}

	open val connectivityModule: ConnectivityModule by lazy {
		ReactiveNetworkConnectivityModule()
	}

	open val localizationModule: LocalizationModule by lazy {
		AndroidLocalizationModule(contextModule)
	}

	open val formattingModule: FormattingModule by lazy {
		DateUtilsFormattingModule(contextModule, localizationModule)
	}

	open val darknessModule: DarknessModule by lazy {
		KlausBrunnerDarknessModule(timeModule, locationModule)
	}

	open val geomagLocationModule: GeomagLocationModule by lazy {
		RealGeomagLocationModule(locationModule)
	}

	open val kpIndexModule: KpIndexModule =
		RealKpIndexModule()

	open val weatherModule: WeatherModule by lazy {
		OpenWeatherMapWeatherModule(openWeatherMapApiKey, locationModule)
	}

	open val locationNameModule: LocationNameModule by lazy {
		GeocoderLocationNameModule(contextModule, locationModule)
	}

	open val auroraReportModule: AuroraReportModule by lazy {
		RealAuroraReportModule(
			timeModule,
			locationModule,
			locationNameModule,
			darknessModule,
			geomagLocationModule,
			kpIndexModule,
			weatherModule
		)
	}

	open val evaluationModule: EvaluationModule by lazy {
		RealEvaluationModule()
	}

	open val sharedPreferencesModule: SharedPreferencesModule by lazy {
		DefaultSharedPreferencesModule(contextModule)
	}

	open val rxSharedPreferencesModule: RxSharedPreferencesModule by lazy {
		RealRxSharedPreferencesModule(sharedPreferencesModule)
	}

	open val settingsModule: SettingsModule by lazy {
		RxSettingsModule(contextModule, rxSharedPreferencesModule)
	}

	open val viewModelsModule: ViewModelsModule by lazy {
		AndroidViewModelsModule(
			fluxModule,
			contextModule,
			evaluationModule,
			formattingModule,
			timeModule
		)
	}

	open val analyticsModule: AnalyticsModule by lazy {
		FirebasedAnalyticsModule(contextModule)
	}

	open val fluxModule: FluxModule by lazy {
		RealFluxModule(auroraReportModule, connectivityModule)
	}

	final override val settings: Settings
		get() = settingsModule.settings
	final override val analytics: Analytics
		get() = analyticsModule.analytics
	final override val store: SkylightStore
		get() = fluxModule.store

	final override fun mainViewModel(activity: FragmentActivity): MainViewModel =
		viewModelsModule.mainViewModel(activity)

	final override val backgroundComponent: BackgroundComponent by lazy {
		SkylightBackgroundComponent(
			contextModule,
			formattingModule,
			evaluationModule,
			settingsModule,
			fluxModule,
			timeModule,
			MainActivity::class.java,
			20.minutes,
			10.minutes
		)
	}

}
