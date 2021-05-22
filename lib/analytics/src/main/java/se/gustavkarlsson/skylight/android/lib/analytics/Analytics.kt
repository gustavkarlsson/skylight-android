package se.gustavkarlsson.skylight.android.lib.analytics

interface Analytics {
    fun logScreen(name: String)
    fun setProperty(name: String, value: Any?)
    fun logEvent(name: String, data: Map<String, Any?>? = null)
    fun logEvent(name: String, key: String, value: Any?) =
        logEvent(name, mapOf(key to value))
}
