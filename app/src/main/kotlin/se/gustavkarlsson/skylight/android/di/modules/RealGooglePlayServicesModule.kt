package se.gustavkarlsson.skylight.android.di.modules

import se.gustavkarlsson.skylight.android.services.GooglePlayServicesChecker
import se.gustavkarlsson.skylight.android.services_impl.GmsGooglePlayServicesChecker

class RealGooglePlayServicesModule(
	private val contextModule: ContextModule
) : GooglePlayServicesModule {

	override val googlePlayServicesChecker: GooglePlayServicesChecker by lazy {
		GmsGooglePlayServicesChecker(contextModule.context)
	}
}
