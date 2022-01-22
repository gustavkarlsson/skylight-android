package se.gustavkarlsson.skylight.android.feature.settings

import dagger.Component
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.lib.settings.SettingsRepository
import se.gustavkarlsson.skylight.android.lib.settings.SettingsComponent as LibSettingsComponent

@Component(
    modules = [SettingsModule::class],
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

@Module
internal object SettingsModule {

    // FIXME clean up
    @Provides
    fun viewModel(
        settingsRepository: SettingsRepository,
    ): SettingsViewModel = SettingsViewModel(settingsRepository)
}
