package se.gustavkarlsson.skylight.android.navigation

// FIXME remove
enum class Screen(val popOnLeave: Boolean) {
	START(true),
	MAIN(false),
	SETTINGS(false),
	ABOUT(false),
	INTRO(true),
	GOOGLE_PLAY_SERVICES(true),
	PICK_PLACE(false);
}
