package se.gustavkarlsson.skylight.android

import se.gustavkarlsson.skylight.android.di.components.AppComponent
import se.gustavkarlsson.skylight.android.di.components.SkylightAppComponent

val appComponent: AppComponent by lazy {
	SkylightAppComponent(Skylight.instance)
}
