package se.gustavkarlsson.skylight.android.modules

import android.content.Context
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.gui.screens.about.AboutViewModel
import se.gustavkarlsson.skylight.android.gui.screens.googleplayservices.GooglePlayServicesViewModel
import se.gustavkarlsson.skylight.android.gui.screens.main.MainViewModel
import se.gustavkarlsson.skylight.android.gui.screens.permission.PermissionViewModel

val viewModelModule = module {

	viewModel {
		val context = get<Context>()
		MainViewModel(
			get(),
			context.getString(R.string.main_your_location),
			context.getString(R.string.error_no_internet),
			get("auroraReport"),
			get(),
			get("chanceLevel"),
			get("darkness"),
			get("darkness"),
			get("geomagLocation"),
			get("geomagLocation"),
			get("kpIndex"),
			get("kpIndex"),
			get("weather"),
			get("weather"),
			get(),
			1.minutes
		)
	}

	viewModel {
		PermissionViewModel(get())
	}

	viewModel {
		GooglePlayServicesViewModel(get())
	}

	viewModel {
		AboutViewModel(get())
	}

}
