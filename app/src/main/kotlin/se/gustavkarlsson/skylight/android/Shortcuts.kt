package se.gustavkarlsson.skylight.android

import se.gustavkarlsson.skylight.android.dagger.components.ApplicationComponent
import se.gustavkarlsson.skylight.android.services.Analytics

val appComponent: ApplicationComponent
	get() = Skylight.instance.component

val analytics: Analytics
	get() = appComponent.getAnalytics()
