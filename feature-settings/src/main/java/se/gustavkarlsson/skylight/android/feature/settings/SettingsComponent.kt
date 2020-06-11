package se.gustavkarlsson.skylight.android.feature.settings

import dagger.Component
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.AppComponent
import se.gustavkarlsson.skylight.android.lib.settings.Settings
import se.gustavkarlsson.skylight.android.lib.settings.SettingsComponent as LibSettingsComponent

@Component(
    modules = [SettingsModule::class],
    dependencies = [
        AppComponent::class,
        LibSettingsComponent::class
    ]
)
internal interface SettingsComponent {
    fun viewModel(): SettingsViewModel

    companion object {
        fun build(): SettingsComponent =
            DaggerSettingsComponent.builder()
                .appComponent(AppComponent.instance)
                .settingsComponent(LibSettingsComponent.instance)
                .build()
    }
}

@Module
internal class SettingsModule {

    @Provides
    fun viewModel(
        settings: Settings
    ): SettingsViewModel = SettingsViewModel(settings)
}
