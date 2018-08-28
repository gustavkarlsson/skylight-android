package se.gustavkarlsson.skylight.android.di.modules

import se.gustavkarlsson.skylight.android.services.providers.GooglePlayServicesProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.GmsGooglePlayServicesProvider

class RealGooglePlayServicesModule(
	private val contextModule: ContextModule
) : GooglePlayServicesModule {

	override val googlePlayServicesProvider: GooglePlayServicesProvider by lazy {
		GmsGooglePlayServicesProvider(contextModule.context)
	}
}
