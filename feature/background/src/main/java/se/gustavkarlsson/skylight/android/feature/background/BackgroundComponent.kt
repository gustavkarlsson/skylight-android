package se.gustavkarlsson.skylight.android.feature.background

import se.gustavkarlsson.skylight.android.feature.background.scheduling.BackgroundWork

interface BackgroundComponent {

    fun backgroundWork(): BackgroundWork

    interface Setter {
        fun setBackgroundComponent(component: BackgroundComponent) {
            instance = component
        }
    }

    companion object {
        lateinit var instance: BackgroundComponent
            private set
    }
}
