package se.gustavkarlsson.skylight.android.feature.googleplayservices

import android.os.Bundle
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import se.gustavkarlsson.skylight.android.navigation.Backstack
import se.gustavkarlsson.skylight.android.navigation.Screen
import se.gustavkarlsson.skylight.android.navigation.ScreenName
import se.gustavkarlsson.skylight.android.navigation.withTarget

@Parcelize
internal data class GooglePlayServicesScreen(private val target: Backstack) :
    Screen {
    @IgnoredOnParcel
    override val name = ScreenName.GooglePlayServices

    override fun createFragment() = GooglePlayServicesFragment().apply {
        arguments = Bundle().withTarget(target)
    }
}
