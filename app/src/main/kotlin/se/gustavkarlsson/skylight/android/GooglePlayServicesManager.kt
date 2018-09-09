package se.gustavkarlsson.skylight.android

import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.services.GooglePlayServicesChecker
import se.gustavkarlsson.skylight.android.services_impl.GmsGooglePlayServicesChecker

val googlePlayServicesModule = module {

	single<GooglePlayServicesChecker> {
		GmsGooglePlayServicesChecker(get())
	}
}
