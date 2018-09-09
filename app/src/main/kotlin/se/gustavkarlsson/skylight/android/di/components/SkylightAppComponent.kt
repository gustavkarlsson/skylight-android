package se.gustavkarlsson.skylight.android.di.components

import android.app.Application
import androidx.fragment.app.Fragment
import se.gustavkarlsson.skylight.android.di.modules.*
import se.gustavkarlsson.skylight.android.gui.screens.googleplayservices.GooglePlayServicesViewModel
import se.gustavkarlsson.skylight.android.gui.screens.main.MainViewModel
import se.gustavkarlsson.skylight.android.gui.screens.permission.PermissionViewModel
import se.gustavkarlsson.skylight.android.krate.SkylightStore
import se.gustavkarlsson.skylight.android.services.Analytics

open class SkylightAppComponent(
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
		OpenWeatherMapWeatherModule(locationModule)
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
			krateModule,
			contextModule,
			evaluationModule,
			formattingModule,
			timeModule
		)
	}

	open val analyticsModule: AnalyticsModule by lazy {
		FirebasedAnalyticsModule(contextModule)
	}

	open val permissionModule: PermissionsModule by lazy {
		RealPermissionModule(contextModule)
	}

	open val googlePlayServicesModule: GooglePlayServicesModule by lazy {
		RealGooglePlayServicesModule(contextModule)
	}

	open val runVersionsModule: RunVersionsModule by lazy {
		RealRunVersionsModule(contextModule)
	}

	open val krateModule: KrateModule by lazy {
		RealKrateModule(
			auroraReportModule,
			connectivityModule,
			settingsModule,
			permissionModule,
			googlePlayServicesModule,
			runVersionsModule
		)
	}

	final override val analytics: Analytics
		get() = analyticsModule.analytics
	final override val store: SkylightStore
		get() = krateModule.store
	final override val locationPermission: String
		get() = locationModule.locationPermission

	final override fun mainViewModel(fragment: Fragment): MainViewModel =
		viewModelsModule.mainViewModel(fragment)

	final override fun permissionViewModel(fragment: Fragment): PermissionViewModel =
		viewModelsModule.permissionViewModel(fragment)

	final override fun googlePlayServicesViewModel(fragment: Fragment): GooglePlayServicesViewModel =
		viewModelsModule.googlePlayServicesViewModel(fragment)

}
