package se.gustavkarlsson.skylight.android.feature.intro

import android.os.Bundle
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import se.gustavkarlsson.skylight.android.lib.navigation.newer.Backstack
import se.gustavkarlsson.skylight.android.lib.navigation.newer.Screen
import se.gustavkarlsson.skylight.android.lib.navigation.newer.ScreenName
import se.gustavkarlsson.skylight.android.lib.navigation.newer.withTarget

@Parcelize
internal data class IntroScreen(private val target: Backstack) : Screen {
    @IgnoredOnParcel
    override val name = ScreenName.Intro

    override fun createFragment() = IntroFragment().apply {
        arguments = Bundle().withTarget(target)
    }
}
