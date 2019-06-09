package se.gustavkarlsson.skylight.android.feature.googleplayservices

import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.ModuleStarter
import se.gustavkarlsson.skylight.android.lib.ui.Destination
import se.gustavkarlsson.skylight.android.lib.ui.DestinationRegistry

val featureGooglePlayServicesModule = module {

	single<GooglePlayServicesChecker> {
		GmsGooglePlayServicesChecker(context = get())
	}

	viewModel {
		GooglePlayServicesViewModel(
			navigator = get()
		)
	}

	single<ModuleStarter>("googleplayservices") {
		val googlePlayServicesChecker = get<GooglePlayServicesChecker>()
		object : ModuleStarter {
			override fun start() {
				val destination = Destination(10) {
					if (!googlePlayServicesChecker.isAvailable)
						GooglePlayServicesFragment()
					else
						null
				}
				get<DestinationRegistry>().register(destination)
			}
		}
	}

}
