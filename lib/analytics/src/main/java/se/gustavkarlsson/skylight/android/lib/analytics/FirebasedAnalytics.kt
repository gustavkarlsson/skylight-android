package se.gustavkarlsson.skylight.android.lib.analytics

import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent

internal class FirebasedAnalytics(
    private val firebaseAnalytics: FirebaseAnalytics,
) : Analytics {

    override fun logScreen(name: String) =
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param("name", name)
        }

    override fun setProperty(name: String, value: Any?) =
        firebaseAnalytics.setUserProperty(name, value?.toString())

    override fun logEvent(name: String, data: Map<String, Any?>?) {
        val bundle = data
            ?.toList()
            ?.toTypedArray()
            ?.let(::bundleOf)
        firebaseAnalytics.logEvent(name, bundle)
    }
}
