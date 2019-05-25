package se.gustavkarlsson.skylight.android.feature.googleplayservices

import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.services.GooglePlayServicesChecker

val googlePlayServicesModule = module {

	single<GooglePlayServicesChecker> {
		GmsGooglePlayServicesChecker(context = get())
	}

	viewModel {
		GooglePlayServicesViewModel(store = get("main"))
	}

}
