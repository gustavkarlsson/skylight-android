package se.gustavkarlsson.skylight.android.di.components

import android.support.v4.app.Fragment
import se.gustavkarlsson.skylight.android.background.di.components.BackgroundComponent
import se.gustavkarlsson.skylight.android.flux.SkylightStore
import se.gustavkarlsson.skylight.android.gui.screens.main.MainViewModel
import se.gustavkarlsson.skylight.android.services.Analytics
import se.gustavkarlsson.skylight.android.services.Settings

interface AppComponent {
	val settings: Settings
	val analytics: Analytics
	val store: SkylightStore

	fun mainViewModel(fragment: Fragment): MainViewModel

	val backgroundComponent: BackgroundComponent
}
