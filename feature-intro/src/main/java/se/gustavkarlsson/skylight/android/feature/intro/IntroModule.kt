package se.gustavkarlsson.skylight.android.feature.intro

import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.services.GooglePlayServicesChecker

val introModule = module {

	viewModel {
		IntroViewModel(store = get("main"))
	}

}
