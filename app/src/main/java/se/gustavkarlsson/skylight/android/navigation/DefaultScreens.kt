package se.gustavkarlsson.skylight.android.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.ExperimentalComposeUiApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import se.gustavkarlsson.skylight.android.feature.about.AboutScreen
import se.gustavkarlsson.skylight.android.feature.intro.PrivacyPolicyScreen
import se.gustavkarlsson.skylight.android.feature.main.view.MainScreen
import se.gustavkarlsson.skylight.android.lib.navigation.Screen
import se.gustavkarlsson.skylight.android.lib.navigation.Screens

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalCoroutinesApi
internal object DefaultScreens : Screens {
    override val main: Screen = MainScreen
    override val about: Screen = AboutScreen
    override val privacyPolicy: Screen = PrivacyPolicyScreen
}
