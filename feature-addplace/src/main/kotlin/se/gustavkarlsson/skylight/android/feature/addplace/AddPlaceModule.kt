package se.gustavkarlsson.skylight.android.feature.addplace

import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.services.Geocoder
import android.location.Geocoder as BuiltInGeocoder

val addPlaceModule = module {

	single<Geocoder> {
		MapboxGeocoder(BuildConfig.MAPBOX_API_KEY)
	}

	viewModel {
		AddPlaceViewModel(
			geocoder = get(),
			sampleDuration = 1.seconds,
			retryDelay = 2.seconds
		)
	}

}
