package se.gustavkarlsson.skylight.android.feature.settings

import com.squareup.anvil.annotations.MergeComponent
import dagger.Component
import se.gustavkarlsson.skylight.android.lib.settings.SettingsComponent as LibSettingsComponent

@MergeComponent(
    scope = SettingsScopeMarker::class,
    dependencies = [LibSettingsComponent::class]
)
internal interface SettingsComponent {
    fun viewModel(): SettingsViewModel

    companion object {
        fun build(): SettingsComponent =
            DaggerSettingsComponent.builder()
                .settingsComponent(LibSettingsComponent.instance)
                .build()
    }
}

abstract class SettingsScopeMarker private constructor()
