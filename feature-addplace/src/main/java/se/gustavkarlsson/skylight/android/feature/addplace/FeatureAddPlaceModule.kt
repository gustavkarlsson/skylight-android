package se.gustavkarlsson.skylight.android.feature.addplace

import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import se.gustavkarlsson.koptional.Optional
import se.gustavkarlsson.skylight.android.ModuleStarter
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.lib.navigation.FragmentFactory
import se.gustavkarlsson.skylight.android.lib.navigation.FragmentFactoryRegistry
import se.gustavkarlsson.skylight.android.lib.navigation.NavItem

val featureAddPlaceModule = module {

	viewModel { (destination: Optional<NavItem>) ->
		AddPlaceViewModel(
			placesRepository  = get(),
			knot = get("addplace"),
			navigator = get(),
			destination = destination.value
		)
	}

	factory("addplace") {
		createKnot(
			geocoder = get(),
			searchSampleDelay = 1.seconds,
			retryDelay = 2.seconds
		)
	}

	single<ModuleStarter>("addplace") {
		object : ModuleStarter {
			override fun start() {
				val fragmentFactory = object : FragmentFactory {
					override fun createFragment(name: String): Fragment? =
						if (name == "addplace") AddPlaceFragment()
						else null
				}
				get<FragmentFactoryRegistry>().register(fragmentFactory)
			}
		}
	}

}
