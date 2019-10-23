package se.gustavkarlsson.skylight.android.services

import android.app.Activity

interface Analytics {
	fun logScreen(activity: Activity, name: String)
	fun setProperty(name: String, value: Any?)
	fun logEvent(name: String, data: Map<String, Any?>? = null)
	fun logEvent(name: String, key: String, value: Any?) =
		logEvent(name, mapOf(key to value))
}
