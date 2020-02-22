package se.gustavkarlsson.skylight.android.feature.googleplayservices

import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.lib.navigation.Backstack
import se.gustavkarlsson.skylight.android.lib.navigation.NavigationOverride

val featureGooglePlayServicesModule = module {

    single<GooglePlayServicesChecker> {
        GmsGooglePlayServicesChecker(context = get())
    }

    factory {
        GooglePlayServicesViewModel()
    }

    single<NavigationOverride>("googleplayservices") {
        object : NavigationOverride {
            val googlePlayServicesChecker = get<GooglePlayServicesChecker>()

            override val priority = 8

            override fun override(backstack: Backstack): Backstack? =
                if (!googlePlayServicesChecker.isAvailable) {
                    listOf(GooglePlayServicesScreen(backstack))
                } else null
        }
    }
}
