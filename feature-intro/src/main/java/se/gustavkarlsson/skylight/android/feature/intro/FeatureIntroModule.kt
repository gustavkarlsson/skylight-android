package se.gustavkarlsson.skylight.android.feature.intro

import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.lib.navigation.Backstack
import se.gustavkarlsson.skylight.android.lib.navigation.NavigationOverride

val featureIntroModule = module {

    viewModel {
        IntroViewModel(versionManager = get())
    }

    single<NavigationOverride>("intro") {
        object : NavigationOverride {
            val runVersionManager = get<RunVersionManager>()

            override val priority = 10

            override fun override(backstack: Backstack): Backstack? =
                if (runVersionManager.isFirstRun) {
                    listOf(IntroScreen(backstack))
                } else null
        }
    }

    single<RunVersionManager> {
        SharedPreferencesRunVersionManager(
            context = get(),
            currentVersionCode = get("versionCode")
        )
    }
}
