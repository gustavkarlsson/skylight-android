package se.gustavkarlsson.skylight.android.navigation

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import se.gustavkarlsson.skylight.android.feature.about.AboutFragment
import se.gustavkarlsson.skylight.android.feature.intro.PrivacyPolicyFragment
import se.gustavkarlsson.skylight.android.feature.main.MainFragment
import se.gustavkarlsson.skylight.android.lib.navigation.Screen
import se.gustavkarlsson.skylight.android.lib.navigation.ScreenName
import se.gustavkarlsson.skylight.android.lib.navigation.Screens

internal object DefaultScreens : Screens {
    override val main: Screen = MainScreen()
    override val about: Screen = AboutScreen()
    override val privacyPolicy: Screen = PrivacyPolicyScreen()
}

@Parcelize
private data class MainScreen(private val dummy: Unit = Unit) : Screen {
    @IgnoredOnParcel
    override val name = ScreenName.Main

    @IgnoredOnParcel
    override val scopeStart: String = "main"

    @ExperimentalCoroutinesApi
    @FlowPreview
    override fun createFragment() = MainFragment()
}

@Parcelize
private data class AboutScreen(private val dummy: Unit = Unit) : Screen {
    @IgnoredOnParcel
    override val name = ScreenName.About

    override fun createFragment() = AboutFragment()
}

@Parcelize
private data class PrivacyPolicyScreen(private val dummy: Unit = Unit) : Screen {
    @IgnoredOnParcel
    override val name = ScreenName.PrivacyPolicy

    override fun createFragment() = PrivacyPolicyFragment()
}
