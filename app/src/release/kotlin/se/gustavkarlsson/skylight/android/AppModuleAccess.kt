package se.gustavkarlsson.skylight.android

import se.gustavkarlsson.skylight.android.di.modules.AppModule
import se.gustavkarlsson.skylight.android.di.modules.SkylightAppModule

val appModule: AppModule by lazy {
	SkylightAppModule(BuildConfig.OPENWEATHERMAP_API_KEY, Skylight.instance)
}
