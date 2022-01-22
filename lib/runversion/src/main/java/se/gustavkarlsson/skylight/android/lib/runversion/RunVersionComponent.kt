package se.gustavkarlsson.skylight.android.lib.runversion

import com.squareup.anvil.annotations.ContributesTo
import se.gustavkarlsson.skylight.android.core.AppScopeMarker

@ContributesTo(AppScopeMarker::class)
interface RunVersionComponent {

    fun runVersionManager(): RunVersionManager

    interface Setter {
        fun setRunVersionComponent(component: RunVersionComponent) {
            instance = component
        }
    }

    companion object {
        lateinit var instance: RunVersionComponent
            private set
    }
}
