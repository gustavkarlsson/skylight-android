package se.gustavkarlsson.skylight.android.gui.screens.permission

import com.agoda.kakao.KTextView
import com.agoda.kakao.KView
import com.agoda.kakao.Screen
import se.gustavkarlsson.skylight.android.R

object PermissionScreen : Screen<PermissionScreen>() {
	val grantButton = KTextView { withId(R.id.grantButton) }
	val locationPermissionRequiredDialog = KView { withText(R.string.permission_required_title) }
	val permissionDeniedDialog = KView { withText(R.string.permission_denied_title) }
	val okButton = KView { withText(android.R.string.ok) }
	val exitButton = KView { withText(R.string.exit) }
	fun isDisplayed() = grantButton.isDisplayed()
}
