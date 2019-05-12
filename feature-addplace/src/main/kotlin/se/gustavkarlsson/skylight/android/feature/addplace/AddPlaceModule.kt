package se.gustavkarlsson.skylight.android.feature.addplace

import org.koin.dsl.module.module
import org.koin.androidx.viewmodel.ext.koin.viewModel

val addPlaceModule = module {

	viewModel {
		AddPlaceViewModel()
	}

}
