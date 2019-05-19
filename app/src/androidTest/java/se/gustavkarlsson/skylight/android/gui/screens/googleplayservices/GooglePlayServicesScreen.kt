package se.gustavkarlsson.skylight.android.gui.screens.googleplayservices

import com.agoda.kakao.KButton
import com.agoda.kakao.Screen
import se.gustavkarlsson.skylight.android.R

object GooglePlayServicesScreen : Screen<GooglePlayServicesScreen>() {
	val installButton = KButton { withId(R.id.installButton) }
}
