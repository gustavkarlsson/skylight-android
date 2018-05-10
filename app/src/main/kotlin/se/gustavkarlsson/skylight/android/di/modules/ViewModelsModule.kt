package se.gustavkarlsson.skylight.android.di.modules

import android.support.v4.app.FragmentActivity
import se.gustavkarlsson.skylight.android.gui.activities.main.MainViewModel

interface ViewModelsModule {
	fun mainViewModel(activity: FragmentActivity): MainViewModel
}
