package se.gustavkarlsson.skylight.android

import se.gustavkarlsson.skylight.android.di.components.AppComponent
import se.gustavkarlsson.skylight.android.di.components.DebugSkylightAppComponent

val appComponent: AppComponent by lazy {
	DebugSkylightAppComponent(BuildConfig.OPENWEATHERMAP_API_KEY, Skylight.instance)
}
