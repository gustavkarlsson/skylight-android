package se.gustavkarlsson.skylight.android.lib.analytics

import android.app.Activity
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics

internal class FirebasedAnalytics(
    private val firebaseAnalytics: FirebaseAnalytics
) : Analytics {

    override fun logScreen(activity: Activity, name: String) =
        firebaseAnalytics.setCurrentScreen(activity, name, name)

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
