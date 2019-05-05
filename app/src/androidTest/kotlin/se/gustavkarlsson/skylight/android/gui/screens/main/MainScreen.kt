package se.gustavkarlsson.skylight.android.gui.screens.main

import com.agoda.kakao.KTextView
import com.agoda.kakao.KView
import com.agoda.kakao.Screen
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.gui.kakao.KBottomSheetDialogFragment
import se.gustavkarlsson.skylight.android.gui.kakao.KOutsideBottomSheetDialogFragment
import se.gustavkarlsson.skylight.android.gui.kakao.KToolbar

object MainScreen : Screen<MainScreen>() {
	val toolbar = KToolbar { withId(R.id.toolbarView) }
	val chance = KTextView { withId(R.id.chance) }
	val timeSinceUpdate = KTextView { withId(R.id.timeSinceUpdate) }

	val kpIndexCard = KView { withId(R.id.kpIndexCard) }
	val darknessCard = KView { withId(R.id.darknessCard) }
	val geomagLocationCard = KView { withId(R.id.geomagLocationCard) }
	val weatherCard = KView { withId(R.id.weatherCard) }

	val detailView = KBottomSheetDialogFragment()
	val outsideDetailView =
		KOutsideBottomSheetDialogFragment()

	fun isDisplayed() {
		chance.isDisplayed()
		kpIndexCard.isDisplayed()
	}
}
