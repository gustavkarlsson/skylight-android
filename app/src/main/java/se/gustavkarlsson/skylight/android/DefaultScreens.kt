package se.gustavkarlsson.skylight.android

import android.os.Bundle
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import se.gustavkarlsson.skylight.android.feature.about.AboutFragment
import se.gustavkarlsson.skylight.android.feature.addplace.AddPlaceFragment
import se.gustavkarlsson.skylight.android.feature.main.gui.MainFragment
import se.gustavkarlsson.skylight.android.feature.settings.SettingsFragment
import se.gustavkarlsson.skylight.android.navigation.Backstack
import se.gustavkarlsson.skylight.android.navigation.Screen
import se.gustavkarlsson.skylight.android.navigation.ScreenName
import se.gustavkarlsson.skylight.android.navigation.Screens
import se.gustavkarlsson.skylight.android.navigation.withTarget

internal object DefaultScreens :
    Screens {
    override val main: Screen = MainScreen()
    override fun addPlace(target: Backstack?): Screen = AddPlaceScreen(target)
    override val settings: Screen = SettingsScreen()
    override val about: Screen = AboutScreen()
}

@Parcelize
private data class MainScreen(private val dummy: Unit = Unit) :
    Screen {
    @IgnoredOnParcel
    override val name = ScreenName.Main
    @IgnoredOnParcel
    override val scopeStart: String = "main"

    override fun createFragment() = MainFragment()
}

@Parcelize
private data class AddPlaceScreen(private val target: Backstack?) :
    Screen {
    @IgnoredOnParcel
    override val name = ScreenName.AddPlace

    override fun createFragment() = AddPlaceFragment().apply {
        if (target != null) {
            arguments = Bundle().withTarget(target)
        }
    }
}

@Parcelize
private data class SettingsScreen(private val dummy: Unit = Unit) :
    Screen {
    @IgnoredOnParcel
    override val name = ScreenName.Settings

    override fun createFragment() = SettingsFragment()
}

@Parcelize
private data class AboutScreen(private val dummy: Unit = Unit) :
    Screen {
    @IgnoredOnParcel
    override val name = ScreenName.About

    override fun createFragment() = AboutFragment()
}
