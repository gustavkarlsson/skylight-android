package se.gustavkarlsson.skylight.android.di.modules

import android.app.Application
import se.gustavkarlsson.skylight.android.gui.activities.main.MainViewModelFactory
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance.AuroraChanceViewModelFactory
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.AuroraFactorsViewModelFactory
import se.gustavkarlsson.skylight.android.services.Analytics
import se.gustavkarlsson.skylight.android.services.Scheduler
import se.gustavkarlsson.skylight.android.services.Settings
import se.gustavkarlsson.skylight.android.services_impl.scheduling.UpdateJob

open class SkylightAppModule(
	openWeatherMapApiKey: String, // TODO Is this really the right way to do this?
	application: Application
) : AppModule {

	open val contextModule: ContextModule by lazy {
		ApplicationContextModule(application)
	}

	open val timeModule: TimeModule by lazy {
		SystemTimeModule()
	}

	open val locationModule: LocationModule by lazy {
		ReactiveLocationModule(contextModule.context)
	}

	open val connectivityModule: ConnectivityModule by lazy {
		ReactiveNetworkConnectivityModule()
	}

	open val localizationModule: LocalizationModule by lazy {
		AndroidLocalizationModule(
			contextModule.context
		)
	}

	open val formattingModule: FormattingModule by lazy {
		DateUtilsFormattingModule(
			contextModule.context,
			localizationModule.locale
		)
	}

	open val darknessModule: DarknessModule by lazy {
		KlausBrunnerDarknessModule(
			timeModule.now,
			locationModule.locationFlowable
		)
	}

	open val geomagLocationModule: GeomagLocationModule by lazy {
		RealGeomagLocationModule(
			locationModule.locationFlowable
		)
	}

	open val kpIndexModule: KpIndexModule = RealKpIndexModule()

	open val visibilityModule: VisibilityModule by lazy {
		OpenWeatherMapVisibilityModule(
			openWeatherMapApiKey,
			locationModule.locationFlowable
		)
	}

	open val locationNameModule: LocationNameModule by lazy {
		GeocoderLocationNameModule(
			contextModule.context,
			locationModule.locationFlowable
		)
	}

	open val auroraReportModule: AuroraReportModule by lazy {
		RealAuroraReportModule(
			timeModule.timeProvider,
			locationModule.locationProvider,
			locationNameModule.locationNameProvider,
			darknessModule.darknessProvider,
			geomagLocationModule.geomagLocationProvider,
			kpIndexModule.kpIndexProvider,
			visibilityModule.visibilityProvider,
			locationNameModule.locationNameFlowable,
			kpIndexModule.kpIndexFlowable,
			geomagLocationModule.geomagLocationFlowable,
			darknessModule.darknessFlowable,
			visibilityModule.visibilityFlowable
		)
	}

	open val evaluationModule: EvaluationModule = RealEvaluationModule()

	open val notifierModule: NotifierModule by lazy {
		AndroidNotifierModule(
			contextModule.context,
			formattingModule.chanceLevelFormatter,
			evaluationModule.auroraReportEvaluator
		)
	}

	open val settingsModule: SettingsModule by lazy { RxSettingsModule(contextModule.context) }

	open val updateSchedulerModule: UpdateSchedulerModule by lazy { RealUpdateSchedulerModule() }

	open val updateJobModule: UpdateJobModule by lazy {
		RealUpdateJobModule(
			contextModule.context,
			timeModule.timeProvider,
			evaluationModule.auroraReportEvaluator,
			settingsModule.settings,
			auroraReportModule.auroraReportSingle,
			notifierModule.notifier
		)
	}

	open val viewModelsModule: ViewModelsModule by lazy {
		AndroidViewModelsModule(
			contextModule.context,
			auroraReportModule.auroraReportFlowable,
			evaluationModule.auroraReportEvaluator,
			formattingModule.relativeTimeFormatter,
			formattingModule.chanceLevelFormatter,
			evaluationModule.darknessEvaluator,
			formattingModule.darknessFormatter,
			evaluationModule.geomagLocationEvaluator,
			formattingModule.geomagLocationFormatter,
			evaluationModule.kpIndexEvaluator,
			formattingModule.kpIndexFormatter,
			evaluationModule.visibilityEvaluator,
			formattingModule.visibilityFormatter,
			auroraReportModule.auroraReportSingle,
			connectivityModule.connectivityFlowable,
			timeModule.now
		)
	}

	open val analyticsModule: AnalyticsModule by lazy { FirebasedAnalyticsModule(contextModule.context) }

	final override val settings: Settings
		get() = settingsModule.settings
	final override val updateScheduler: Scheduler
		get() = updateSchedulerModule.updateScheduler
	final override val updateJob: UpdateJob
		get() = updateJobModule.updateJob
	final override val auroraChanceViewModelFactory: AuroraChanceViewModelFactory
		get() = viewModelsModule.auroraChanceViewModelFactory
	final override val auroraFactorsViewModelFactory: AuroraFactorsViewModelFactory
		get() = viewModelsModule.auroraFactorsViewModelFactory
	final override val mainViewModelFactory: MainViewModelFactory
		get() = viewModelsModule.mainViewModelFactory
	final override val analytics: Analytics
		get() = analyticsModule.analytics
}
