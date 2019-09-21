package se.gustavkarlsson.skylight.android.feature.addplace

import androidx.fragment.app.Fragment
import com.ioki.textref.TextRef
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import se.gustavkarlsson.koptional.Optional
import se.gustavkarlsson.skylight.android.ModuleStarter
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.lib.navigation.FragmentFactory
import se.gustavkarlsson.skylight.android.lib.navigation.FragmentFactoryRegistry
import se.gustavkarlsson.skylight.android.lib.navigation.NavItem

val featureAddPlaceModule = module {

	single("addplace_errors") {
		PublishRelay.create<TextRef>()
	}

	single<Consumer<TextRef>>("addplace_errors") {
		get<PublishRelay<TextRef>>("addplace_errors")
	}

	single<Observable<TextRef>>("addplace_errors") {
		get<PublishRelay<TextRef>>("addplace_errors")
	}

	viewModel { (destination: Optional<NavItem>) ->
		AddPlaceViewModel(
			placesRepository  = get(),
			knot = get("addplace"),
			navigator = get(),
			destination = destination.value,
			errorMessages = get("addplace_errors")
		)
	}

	factory("addplace") {
		createKnot(
			geocoder = get(),
			querySampleDelay = 1.seconds,
			errorMessageConsumer = get("addplace_errors")
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
