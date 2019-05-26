package se.gustavkarlsson.skylight.android.feature.googleplayservices

import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.ModuleStarter
import se.gustavkarlsson.skylight.android.feature.base.Destination
import se.gustavkarlsson.skylight.android.feature.base.DestinationRegistry
import se.gustavkarlsson.skylight.android.services.GooglePlayServicesChecker

val googlePlayServicesModule = module {

	single<GooglePlayServicesChecker> {
		GmsGooglePlayServicesChecker(context = get())
	}

	viewModel { (targetId: String) ->
		GooglePlayServicesViewModel(
			navigator = get(),
			targetId = targetId
		)
	}

	single<ModuleStarter>("googleplayservices") {
		val googlePlayServicesChecker = get<GooglePlayServicesChecker>()
		object : ModuleStarter {
			override fun start() {
				val destination = Destination("googleplayservices", 10, false) { id ->
					if (!googlePlayServicesChecker.isAvailable)
						GooglePlayServicesFragment.newInstance(id)
					else
						null
				}
				get<DestinationRegistry>().register(destination)
			}
		}
	}

}
