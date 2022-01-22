package se.gustavkarlsson.skylight.android.lib.analytics

import com.squareup.anvil.annotations.ContributesTo
import se.gustavkarlsson.skylight.android.core.AppScopeMarker

@ContributesTo(AppScopeMarker::class)
interface AnalyticsComponent {

    fun analytics(): Analytics

    interface Setter {
        fun setAnalyticsComponent(component: AnalyticsComponent) {
            instance = component
        }
    }

    companion object {
        lateinit var instance: AnalyticsComponent
            private set
    }
}
