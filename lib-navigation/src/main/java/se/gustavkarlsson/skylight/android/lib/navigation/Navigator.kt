package se.gustavkarlsson.skylight.android.lib.navigation

interface Navigator {
	fun goTo(destination: String, arguments: Map<String, String> = emptyMap())
	fun goBack(): Boolean
}
