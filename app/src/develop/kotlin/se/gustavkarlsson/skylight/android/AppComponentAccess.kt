package se.gustavkarlsson.skylight.android

import se.gustavkarlsson.skylight.android.di.components.AppComponent
import se.gustavkarlsson.skylight.android.di.components.DevelopSkylightAppComponent

val appComponent: AppComponent by lazy {
	DevelopSkylightAppComponent(Skylight.instance)
}
