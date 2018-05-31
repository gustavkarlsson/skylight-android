package se.gustavkarlsson.skylight.android

import se.gustavkarlsson.skylight.android.di.components.AppComponent
import se.gustavkarlsson.skylight.android.di.components.TestSkylightAppComponent

val testAppComponent: TestSkylightAppComponent by lazy {
	TestSkylightAppComponent(Skylight.instance)
}

val appComponent: AppComponent by lazy { testAppComponent }
