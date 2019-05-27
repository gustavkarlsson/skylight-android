package se.gustavkarlsson.skylight.android.lib.ui

interface BackButtonHandler {
	/**
	 * @return `true` - indicates that the owner has consumed
	 *
	 * `false` - indicates that the caller should handle it
	 */
	fun onBackPressed(): Boolean
}
