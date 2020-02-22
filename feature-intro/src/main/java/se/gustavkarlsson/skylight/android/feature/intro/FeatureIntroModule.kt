package se.gustavkarlsson.skylight.android.feature.intro

import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.navigation.Backstack
import se.gustavkarlsson.skylight.android.navigation.NavigationOverride

val featureIntroModule = module {

    factory {
        IntroViewModel(versionManager = get())
    }

    single<NavigationOverride>("intro") {
        object :
            NavigationOverride {
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
