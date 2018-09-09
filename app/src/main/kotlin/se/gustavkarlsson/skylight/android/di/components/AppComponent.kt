package se.gustavkarlsson.skylight.android.di.components

import androidx.fragment.app.Fragment
import se.gustavkarlsson.skylight.android.gui.screens.googleplayservices.GooglePlayServicesViewModel
import se.gustavkarlsson.skylight.android.gui.screens.main.MainViewModel
import se.gustavkarlsson.skylight.android.gui.screens.permission.PermissionViewModel
import se.gustavkarlsson.skylight.android.services.Analytics

interface AppComponent {
	val analytics: Analytics

	fun mainViewModel(fragment: Fragment): MainViewModel
	fun permissionViewModel(fragment: Fragment): PermissionViewModel
	fun googlePlayServicesViewModel(fragment: Fragment): GooglePlayServicesViewModel
}
