package se.gustavkarlsson.skylight.android.modules

import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.services.Analytics
import se.gustavkarlsson.skylight.android.services_impl.FirebasedAnalytics

val analyticsModule = module {

	single<Analytics> {
		FirebasedAnalytics(get())
	}

}
