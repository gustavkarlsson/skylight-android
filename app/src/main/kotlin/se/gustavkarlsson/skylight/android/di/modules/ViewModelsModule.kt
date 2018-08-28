package se.gustavkarlsson.skylight.android.di.modules

import android.support.v4.app.Fragment
import se.gustavkarlsson.skylight.android.gui.screens.main.MainViewModel
import se.gustavkarlsson.skylight.android.gui.screens.setup.SetupViewModel

interface ViewModelsModule {
	fun mainViewModel(fragment: Fragment): MainViewModel
	fun setupViewModel(fragment: Fragment): SetupViewModel
}
