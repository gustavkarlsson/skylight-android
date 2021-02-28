package se.gustavkarlsson.skylight.android.feature.googleplayservices

import android.os.Bundle
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import se.gustavkarlsson.skylight.android.lib.navigation.Backstack
import se.gustavkarlsson.skylight.android.lib.navigation.Screen
import se.gustavkarlsson.skylight.android.lib.navigation.ScreenName
import se.gustavkarlsson.skylight.android.lib.navigation.withTarget

@Parcelize
internal data class GooglePlayServicesScreen(private val target: Backstack) : Screen {

    init {
        require(target.isNotEmpty()) { "Target backstack must not be empty" }
    }

    @IgnoredOnParcel
    override val name = ScreenName.GooglePlayServices

    override fun createFragment() = GooglePlayServicesFragment().apply {
        arguments = Bundle().withTarget(target)
    }
}
