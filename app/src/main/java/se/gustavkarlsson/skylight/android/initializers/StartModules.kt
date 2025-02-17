package se.gustavkarlsson.skylight.android.initializers

import se.gustavkarlsson.skylight.android.feature.background.BackgroundComponent
import se.gustavkarlsson.skylight.android.lib.settings.LibSettingsComponent

internal fun startModules() {
    val moduleStarters = listOf(
        BackgroundComponent.instance.moduleStarter,
        LibSettingsComponent.instance.moduleStarter,
    )
    for (starter in moduleStarters) {
        starter.start()
    }
}
