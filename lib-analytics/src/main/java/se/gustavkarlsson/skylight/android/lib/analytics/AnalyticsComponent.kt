package se.gustavkarlsson.skylight.android.lib.analytics

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
