package se.gustavkarlsson.skylight.android.navigation

import androidx.annotation.IdRes
import se.gustavkarlsson.skylight.android.R

enum class Screen(@IdRes val id: Int, val alwaysOnTop: Boolean) {
	MAIN(R.id.mainFragment, false),
	SETTINGS(R.id.settingsFragment, false),
	ABOUT(R.id.aboutFragment, false),
	INTRO(R.id.introFragment, true),
	GOOGLE_PLAY_SERVICES(R.id.googlePlayServicesFragment, true),
	PERMISSION(R.id.permissionFragment, true);

	companion object {
		fun fromId(@IdRes id: Int): Screen {
			return Screen.values().find { it.id == id }
				?: throw IllegalArgumentException("No screen with id $id found")
		}
	}
}