package se.gustavkarlsson.skylight.android.feature.about

import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.services.GooglePlayServicesChecker

val aboutModule = module {

	viewModel {
		// FIXME make sure it this works
		val isDevelopMode = BuildConfig.FLAVOR.contains("develop", true)
		AboutViewModel(isDevelopMode = isDevelopMode, time = get())
	}

}
