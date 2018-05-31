package se.gustavkarlsson.skylight.android

import se.gustavkarlsson.skylight.android.di.components.AppComponent
import se.gustavkarlsson.skylight.android.di.components.DebugSkylightAppComponent

val appComponent: AppComponent by lazy {
	DebugSkylightAppComponent(Skylight.instance)
}
