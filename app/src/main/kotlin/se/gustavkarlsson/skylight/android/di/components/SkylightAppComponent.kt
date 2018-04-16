package se.gustavkarlsson.skylight.android.di.components

import android.app.Application
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import se.gustavkarlsson.skylight.android.background.di.components.BackgroundComponent
import se.gustavkarlsson.skylight.android.background.di.components.SkylightBackgroundComponent
import se.gustavkarlsson.skylight.android.di.modules.*
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.gui.activities.main.MainActivity
import se.gustavkarlsson.skylight.android.gui.activities.main.MainViewModel
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance.AuroraChanceViewModel
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.DarknessViewModel
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.GeomagLocationViewModel
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.KpIndexViewModel
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.VisibilityViewModel
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

	open val visibilityModule: VisibilityModule by lazy {
		OpenWeatherMapVisibilityModule(openWeatherMapApiKey, locationModule)
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
			visibilityModule
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
			contextModule,
			auroraReportModule,
			evaluationModule,
			formattingModule,
			connectivityModule,
			timeModule
		)
	}

	open val analyticsModule: AnalyticsModule by lazy {
		FirebasedAnalyticsModule(contextModule)
	}

	final override val settings: Settings
		get() = settingsModule.settings
	final override val analytics: Analytics
		get() = analyticsModule.analytics

	final override fun auroraChanceViewModel(fragment: Fragment): AuroraChanceViewModel =
		viewModelsModule.auroraChanceViewModel(fragment)

	final override fun darknessViewModel(fragment: Fragment): DarknessViewModel =
		viewModelsModule.darknessViewModel(fragment)

	final override fun geomagLocationViewModel(fragment: Fragment): GeomagLocationViewModel =
		viewModelsModule.geomagLocationViewModel(fragment)

	final override fun kpIndexViewModel(fragment: Fragment): KpIndexViewModel =
		viewModelsModule.kpIndexViewModel(fragment)

	final override fun visibilityViewModel(fragment: Fragment): VisibilityViewModel =
		viewModelsModule.visibilityViewModel(fragment)

	final override fun mainViewModel(activity: FragmentActivity): MainViewModel =
		viewModelsModule.mainViewModel(activity)

	final override val backgroundComponent: BackgroundComponent by lazy {
		SkylightBackgroundComponent(
			contextModule,
			formattingModule,
			evaluationModule,
			settingsModule,
			auroraReportModule,
			timeModule,
			MainActivity::class.java,
			20.minutes,
			10.minutes
		)
	}

}
