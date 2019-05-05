package se.gustavkarlsson.skylight.android.gui.screens.permission

import com.agoda.kakao.KTextView
import com.agoda.kakao.Screen
import se.gustavkarlsson.skylight.android.R

class PermissionScreen : Screen<PermissionScreen>() {
	val grantButton = KTextView { withId(R.id.grantButton) }
	fun isDisplayed() = grantButton.isDisplayed()
}
