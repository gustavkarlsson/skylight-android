package se.gustavkarlsson.skylight.android.navigation

import se.gustavkarlsson.skylight.android.feature.about.AboutScreen
import se.gustavkarlsson.skylight.android.feature.intro.PrivacyPolicyScreen
import se.gustavkarlsson.skylight.android.feature.main.view.MainScreen
import se.gustavkarlsson.skylight.android.feature.settings.SettingsScreen
import se.gustavkarlsson.skylight.android.lib.navigation.Screen
import se.gustavkarlsson.skylight.android.lib.navigation.Screens

internal object DefaultScreens : Screens {
    override val main: Screen = MainScreen
    override val about: Screen = AboutScreen
    override val settings: Screen = SettingsScreen
    override val privacyPolicy: Screen = PrivacyPolicyScreen
}
