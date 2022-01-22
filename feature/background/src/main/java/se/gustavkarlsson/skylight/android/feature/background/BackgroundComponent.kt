package se.gustavkarlsson.skylight.android.feature.background

import com.squareup.anvil.annotations.ContributesTo
import se.gustavkarlsson.skylight.android.core.AppScopeMarker
import se.gustavkarlsson.skylight.android.feature.background.scheduling.BackgroundWork

@ContributesTo(AppScopeMarker::class)
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
