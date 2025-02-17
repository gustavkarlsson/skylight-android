package se.gustavkarlsson.skylight.android.feature.settings

import me.tatarka.inject.annotations.Component
import se.gustavkarlsson.skylight.android.core.ViewModelScope
import se.gustavkarlsson.skylight.android.lib.settings.LibSettingsComponent

@Component
@ViewModelScope
internal abstract class SettingsComponent(
    @Component internal val settingsComponent: LibSettingsComponent,
) {
    abstract fun viewModel(): SettingsViewModel

    companion object {
        fun build(): SettingsComponent = SettingsComponent::class.create(
            settingsComponent = LibSettingsComponent.instance,
        )
    }
}
