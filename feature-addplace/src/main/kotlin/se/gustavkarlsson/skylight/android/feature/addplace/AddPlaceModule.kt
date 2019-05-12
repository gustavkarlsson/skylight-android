package se.gustavkarlsson.skylight.android.feature.addplace

import android.location.Geocoder as BuiltInGeocoder
import org.koin.dsl.module.module
import org.koin.androidx.viewmodel.ext.koin.viewModel
import se.gustavkarlsson.skylight.android.services.Geocoder

val addPlaceModule = module {

	single<Geocoder> {
		AndroidGeocoder(BuiltInGeocoder(get()))
	}

	viewModel {
		AddPlaceViewModel(geocoder = get())
	}

}
