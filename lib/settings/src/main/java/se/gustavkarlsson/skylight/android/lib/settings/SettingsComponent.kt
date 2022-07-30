package se.gustavkarlsson.skylight.android.lib.settings

import com.squareup.anvil.annotations.ContributesTo
import se.gustavkarlsson.skylight.android.core.AppScopeMarker

@ContributesTo(AppScopeMarker::class)
interface SettingsComponent {

    fun settings(): SettingsRepository

    interface Setter {
        fun setSettingsComponent(component: SettingsComponent) {
            instance = component
        }
    }

    companion object {
        lateinit var instance: SettingsComponent
            private set
    }
}
