package se.gustavkarlsson.skylight.android.feature.intro

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.multibindings.IntoSet
import javax.inject.Named
import javax.inject.Singleton
import se.gustavkarlsson.skylight.android.navigation.Backstack
import se.gustavkarlsson.skylight.android.navigation.NavigationOverride
import se.gustavkarlsson.skylight.android.services.RunVersionManager

@Module
class FeatureIntroModule {

    @Provides
    @Singleton
    internal fun runVersionManager(
        context: Context,
        @Named("versionCode") versionCode: Int
    ): RunVersionManager = SharedPreferencesRunVersionManager(context, versionCode)

    @Provides
    @Reusable
    @IntoSet
    internal fun navigationOverride(runVersionManager: RunVersionManager): NavigationOverride =
        object : NavigationOverride {
            override val priority = 10

            override fun override(backstack: Backstack): Backstack? =
                if (runVersionManager.isFirstRun) {
                    listOf(IntroScreen(backstack))
                } else null
        }
}
