package se.gustavkarlsson.skylight.android.initializers

import android.app.Application
import se.gustavkarlsson.skylight.android.BuildConfig
import se.gustavkarlsson.skylight.android.MainActivity
import se.gustavkarlsson.skylight.android.core.CoreComponent
import se.gustavkarlsson.skylight.android.core.create
import se.gustavkarlsson.skylight.android.feature.googleplayservices.GooglePlayServicesComponent
import se.gustavkarlsson.skylight.android.feature.intro.IntroComponent
import se.gustavkarlsson.skylight.android.lib.navigation.NavigationComponent
import se.gustavkarlsson.skylight.android.lib.navigation.create
import se.gustavkarlsson.skylight.android.navigation.DefaultScreens

internal fun Application.initDependencies() {
    CoreComponent.instance = CoreComponent::class.create(
        application = this,
        versionCode = BuildConfig.VERSION_CODE,
        versionName = BuildConfig.VERSION_NAME,
        activityClass = MainActivity::class.java,
    )
    NavigationComponent.instance = NavigationComponent::class.create(
        screens = DefaultScreens,
        navigationOverrides = setOf(
            IntroComponent.instance.navigationOverride,
            GooglePlayServicesComponent.instance.navigationOverride,
        ),
    )
}
