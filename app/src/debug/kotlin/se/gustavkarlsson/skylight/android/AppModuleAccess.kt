package se.gustavkarlsson.skylight.android

import se.gustavkarlsson.skylight.android.di.modules.AppModule
import se.gustavkarlsson.skylight.android.di.modules.DebugSkylightAppModule

val appModule: AppModule by lazy {
	DebugSkylightAppModule(BuildConfig.OPENWEATHERMAP_API_KEY, Skylight.instance)
}
