package se.gustavkarlsson.skylight.android.modules

import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.BuildConfig
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.gui.screens.about.AboutViewModel
import se.gustavkarlsson.skylight.android.gui.screens.googleplayservices.GooglePlayServicesViewModel
import se.gustavkarlsson.skylight.android.gui.screens.intro.IntroViewModel
import se.gustavkarlsson.skylight.android.gui.screens.main.MainViewModel
import se.gustavkarlsson.skylight.android.gui.screens.main.drawer.DrawerViewModel
import se.gustavkarlsson.skylight.android.util.ChanceToColorConverter

val viewModelModule = module {

	single {
		ChanceToColorConverter(context = get())
	}

	viewModel {
		MainViewModel(
			store = get("main"),
			auroraChanceEvaluator = get("auroraReport"),
			relativeTimeFormatter = get(),
			chanceLevelFormatter = get("chanceLevel"),
			darknessChanceEvaluator = get("darkness"),
			darknessFormatter = get("darkness"),
			geomagLocationChanceEvaluator = get("geomagLocation"),
			geomagLocationFormatter = get("geomagLocation"),
			kpIndexChanceEvaluator = get("kpIndex"),
			kpIndexFormatter = get("kpIndex"),
			weatherChanceEvaluator = get("weather"),
			weatherFormatter = get("weather"),
			chanceToColorConverter = get(),
			time = get(),
			nowTextThreshold = 1.minutes
		)
	}

	viewModel {
		GooglePlayServicesViewModel(store = get("main"))
	}

	viewModel {
		val isDevelopMode = BuildConfig.FLAVOR.contains("develop", true)
		AboutViewModel(isDevelopMode = isDevelopMode, time = get())
	}

	viewModel {
		IntroViewModel(store = get("main"))
	}

	viewModel {
		DrawerViewModel(store = get("main"), navigator = get())
	}

}
