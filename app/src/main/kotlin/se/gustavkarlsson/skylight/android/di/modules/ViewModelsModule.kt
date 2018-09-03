package se.gustavkarlsson.skylight.android.di.modules

import androidx.fragment.app.Fragment
import se.gustavkarlsson.skylight.android.gui.screens.googleplayservices.GooglePlayServicesViewModel
import se.gustavkarlsson.skylight.android.gui.screens.main.MainViewModel
import se.gustavkarlsson.skylight.android.gui.screens.permission.PermissionViewModel

interface ViewModelsModule {
	fun mainViewModel(fragment: Fragment): MainViewModel
	fun permissionViewModel(fragment: Fragment): PermissionViewModel
	fun googlePlayServicesViewModel(fragment: Fragment): GooglePlayServicesViewModel
}
