package se.gustavkarlsson.skylight.android.feature.googleplayservices

import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.lib.navigation.newer.Backstack
import se.gustavkarlsson.skylight.android.lib.navigation.newer.NavigationOverride

val featureGooglePlayServicesModule = module {

    single<GooglePlayServicesChecker> {
        GmsGooglePlayServicesChecker(context = get())
    }

    viewModel {
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
