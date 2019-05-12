package se.gustavkarlsson.skylight.android.navigation

import androidx.annotation.IdRes
import se.gustavkarlsson.skylight.android.R

enum class Screen(@IdRes val id: Int, val popOnLeave: Boolean) {
	START(R.id.startFragment, true),
	MAIN(R.id.mainFragment, false),
	SETTINGS(R.id.settingsFragment, false),
	ABOUT(R.id.aboutFragment, false),
	INTRO(R.id.introFragment, true),
	GOOGLE_PLAY_SERVICES(R.id.googlePlayServicesFragment, true),
	PERMISSION(R.id.permissionFragment, true),
	PICK_PLACE(R.id.placePickerFragment, false);

	companion object {
		fun fromId(@IdRes id: Int): Screen {
			return values().find { it.id == id }
				?: throw IllegalArgumentException("No screen with id $id found")
		}
	}
}
