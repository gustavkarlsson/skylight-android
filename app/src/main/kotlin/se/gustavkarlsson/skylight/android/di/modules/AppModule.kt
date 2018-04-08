package se.gustavkarlsson.skylight.android.di.modules

interface AppModule :
	ContextModule,
	TimeModule,
	LocationModule,
	ConnectivityModule,
	LocalizationModule,
	FormattingModule,
	DarknessModule,
	GeomagLocationModule,
	KpIndexModule,
	VisibilityModule,
	LocationNameModule,
	AuroraReportModule,
	EvaluationModule,
	NotifierModule,
	SettingsModule,
	UpdateSchedulerModule,
	UpdateJobModule,
	ViewModelsModule
