package se.gustavkarlsson.skylight.android.modules

import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.BuildConfig
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.services_impl.providers.CombiningAuroraReportProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.DevelopAuroraReportProvider

val auroraReportModule = module {

	single {
		CombiningAuroraReportProvider(
			locationProvider = get(),
			reverseGeocoder = get(),
			darknessProvider = get(),
			geomagLocationProvider = get(),
			kpIndexProvider = get(),
			weatherProvider = get()
		)
	}

	single {
		DevelopAuroraReportProvider(
			realProvider = get<CombiningAuroraReportProvider>(),
			developSettings = get(),
			time = get(),
			pollingInterval = 1.minutes
		)
	}

	single {
		@Suppress("ConstantConditionIf")
		if (BuildConfig.DEVELOP) {
			get<DevelopAuroraReportProvider>()
		} else {
			get<CombiningAuroraReportProvider>()
		}
	}
}
