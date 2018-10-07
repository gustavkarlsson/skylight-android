package se.gustavkarlsson.skylight.android.extensions

import androidx.appcompat.widget.Toolbar
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.navigation.Navigator

fun Toolbar.enableBackNavigation(navigator: Navigator) {
	setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
	setNavigationOnClickListener {
		navigator.goBack()
	}
}
