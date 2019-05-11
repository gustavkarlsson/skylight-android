package se.gustavkarlsson.skylight.android.modules

import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.services.DevelopSettings
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.services_impl.RxPreferencesDevelopSettings
import se.gustavkarlsson.skylight.android.services_impl.providers.CombiningAuroraReportProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.DevelopAuroraReportProvider

val developAuroraReportModule = module {

	single<DevelopSettings> {
		RxPreferencesDevelopSettings(get(), get())
	}

	single<AuroraReportProvider>(override = true) {
		val realProvider = get<CombiningAuroraReportProvider>()
		DevelopAuroraReportProvider(realProvider, get(), get(), 1.minutes)
	}
}
