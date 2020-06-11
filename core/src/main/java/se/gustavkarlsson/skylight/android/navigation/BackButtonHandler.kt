package se.gustavkarlsson.skylight.android.navigation

interface BackButtonHandler {
    /**
	 * @return `true` - indicates that the owner has consumed the event
	 *
	 * `false` - indicates that the caller should handle it
	 */
    fun onBackPressed(): Boolean // FIXME use this or BackButtonController?
}
