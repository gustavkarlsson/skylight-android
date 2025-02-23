package se.gustavkarlsson.skylight.android.feature.settings

import me.tatarka.inject.annotations.Component
import se.gustavkarlsson.skylight.android.core.ViewModelScope
import se.gustavkarlsson.skylight.android.lib.settings.SettingsComponent

@Component
@ViewModelScope
internal abstract class SettingsViewModelComponent(
    @Component internal val settingsComponent: SettingsComponent,
) {
    abstract fun viewModel(): SettingsViewModel

    companion object {
        fun build(): SettingsViewModelComponent = SettingsViewModelComponent::class.create(
            settingsComponent = SettingsComponent.instance,
        )
    }
}
