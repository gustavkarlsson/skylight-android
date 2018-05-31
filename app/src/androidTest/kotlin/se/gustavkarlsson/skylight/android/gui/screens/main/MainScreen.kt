package se.gustavkarlsson.skylight.android.gui.screens.main

import com.agoda.kakao.*
import se.gustavkarlsson.skylight.android.R

class MainScreen : Screen<MainScreen>() {
	val swipeRefreshLayout = KSwipeRefreshLayout { withId(R.id.swipeRefreshLayout) }
	val locationName = KTextView { withId(R.id.locationName) }
	val chance = KTextView { withId(R.id.chance) }
	val timeSinceUpdate = KTextView { withId(R.id.timeSinceUpdate) }

	val kpIndexCard = KView { withId(R.id.kpIndexCard) }
	val darknessCard = KView { withId(R.id.darknessCard) }
	val geomagLocationCard = KView { withId(R.id.geomagLocationCard) }
	val weatherCard = KView { withId(R.id.weatherCard) }

	val detailView = KView { withId(android.support.design.R.id.design_bottom_sheet) }
	val outsideDetailView = KView { withId(android.support.design.R.id.touch_outside) }
	val snackbar = KSnackbar()
}
