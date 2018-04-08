package se.gustavkarlsson.skylight.android

import se.gustavkarlsson.skylight.android.di.components.AppComponent
import se.gustavkarlsson.skylight.android.di.components.TestSkylightAppComponent

val testappComponent: TestSkylightAppComponent by lazy {
	TestSkylightAppComponent(BuildConfig.OPENWEATHERMAP_API_KEY, Skylight.instance)
}

val appComponent: AppComponent by lazy { testappComponent }
