package se.gustavkarlsson.skylight.android.di.modules

class SkylightAppModule(
	openWeatherMapApiKey: String, // TODO Is this really the right way to do this?
	contextModule: ContextModule,
	timeModule: TimeModule = SystemTimeModule(),
	locationModule: LocationModule = ReactiveLocationModule(contextModule.context),
	connectivityModule: ConnectivityModule = ReactiveNetworkConnectivityModule(),
	localizationModule: LocalizationModule = AndroidLocalizationModule(contextModule.context),
	formattingModule: FormattingModule = DateUtilsFormattingModule(
		contextModule.context,
		localizationModule.locale
	),
	darknessModule: DarknessModule = KlausBrunnerDarknessModule(
		timeModule.now,
		locationModule.locationFlowable
	),
	geomagLocationModule: GeomagLocationModule = RealGeomagLocationModule(
		locationModule.locationFlowable
	),
	kpIndexModule: KpIndexModule = RealKpIndexModule(),
	visibilityModule: VisibilityModule = OpenWeatherMapVisibilityModule(
		openWeatherMapApiKey,
		locationModule.locationFlowable
	),
	locationNameModule: LocationNameModule = GeocoderLocationNameModule(
		contextModule.context,
		locationModule.locationFlowable
	),
	auroraReportModule: AuroraReportModule = RealAuroraReportModule(
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
	),
	evaluationModule: EvaluationModule = RealEvaluationModule(),
	notifierModule: NotifierModule = AndroidNotifierModule(
		contextModule.context,
		formattingModule.chanceLevelFormatter,
		evaluationModule.auroraReportEvaluator
	),
	settingsModule: SettingsModule = RxSettingsModule(contextModule.context),
	updateSchedulerModule: UpdateSchedulerModule = RealUpdateSchedulerModule(),
	updateJobModule: UpdateJobModule = RealUpdateJobModule(
		contextModule.context,
		timeModule.timeProvider,
		evaluationModule.auroraReportEvaluator,
		settingsModule.settings,
		auroraReportModule.auroraReportSingle,
		notifierModule.notifier
	),
	viewModelsModule: ViewModelsModule = AndroidViewModelsModule(
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
) : AppModule,
	ContextModule by contextModule,
	TimeModule by timeModule,
	LocationModule by locationModule,
	ConnectivityModule by connectivityModule,
	LocalizationModule by localizationModule,
	FormattingModule by formattingModule,
	DarknessModule by darknessModule,
	GeomagLocationModule by geomagLocationModule,
	KpIndexModule by kpIndexModule,
	VisibilityModule by visibilityModule,
	LocationNameModule by locationNameModule,
	AuroraReportModule by auroraReportModule,
	EvaluationModule by evaluationModule,
	NotifierModule by notifierModule,
	SettingsModule by settingsModule,
	UpdateSchedulerModule by updateSchedulerModule,
	UpdateJobModule by updateJobModule,
	ViewModelsModule by viewModelsModule
