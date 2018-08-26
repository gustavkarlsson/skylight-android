package se.gustavkarlsson.skylight.android.di.components

import android.support.v4.app.Fragment
import se.gustavkarlsson.skylight.android.background.di.components.BackgroundComponent
import se.gustavkarlsson.skylight.android.krate.SkylightStore
import se.gustavkarlsson.skylight.android.gui.screens.main.MainViewModel
import se.gustavkarlsson.skylight.android.services.Analytics

interface AppComponent {
	val analytics: Analytics
	val store: SkylightStore
	val locationPermission: String

	fun mainViewModel(fragment: Fragment): MainViewModel

	val backgroundComponent: BackgroundComponent
}
