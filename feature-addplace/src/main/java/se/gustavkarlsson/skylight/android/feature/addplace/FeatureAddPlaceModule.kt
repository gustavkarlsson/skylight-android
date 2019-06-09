package se.gustavkarlsson.skylight.android.feature.addplace

import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.ModuleStarter
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.lib.ui.Destination
import se.gustavkarlsson.skylight.android.lib.ui.DestinationRegistry
import android.location.Geocoder as BuiltInGeocoder

val featureAddPlaceModule = module {

	viewModel {
		AddPlaceViewModel(
			mainStore  = get("main"),
			addPlaceStore = get("addplace"),
			navigator = get()
		)
	}

	factory("addplace") {
		createStore(
			geocoder = get(),
			searchDebounceDelay = 1.seconds,
			retryDelay = 2.seconds
		)
	}

	single<ModuleStarter>("addplace") {
		object : ModuleStarter {
			override fun start() {
				val destination = Destination(0) { id ->
					if (id == "addplace")
						AddPlaceFragment()
					else
						null
				}
				get<DestinationRegistry>().register(destination)
			}
		}
	}

}
