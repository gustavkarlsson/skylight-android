package se.gustavkarlsson.skylight.android.lib.time

import com.squareup.anvil.annotations.ContributesTo
import se.gustavkarlsson.skylight.android.core.AppScopeMarker

@ContributesTo(AppScopeMarker::class)
interface TimeComponent {

    fun time(): Time

    interface Setter {
        fun setTimeComponent(component: TimeComponent) {
            instance = component
        }
    }

    companion object {
        lateinit var instance: TimeComponent
            private set
    }
}
