package se.gustavkarlsson.skylight.android.gui.screens.intro

import com.agoda.kakao.KButton
import com.agoda.kakao.KTextView
import com.agoda.kakao.Screen
import se.gustavkarlsson.skylight.android.R

object IntroScreen : Screen<IntroScreen>() {
	val privacyPolicyLink = KTextView { withId(R.id.privacyPolicyLink) }
	val nextButton = KButton { withId(R.id.nextButton) }
}
