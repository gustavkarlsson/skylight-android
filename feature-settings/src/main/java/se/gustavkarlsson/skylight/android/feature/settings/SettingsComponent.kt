package se.gustavkarlsson.skylight.android.feature.settings

import dagger.Component
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.AppComponent
import se.gustavkarlsson.skylight.android.appComponent
import se.gustavkarlsson.skylight.android.services.Settings

@Component(
    modules = [SettingsModule::class],
    dependencies = [AppComponent::class]
)
internal interface SettingsComponent {
    fun viewModel(): SettingsViewModel

    companion object {
        fun viewModel(): SettingsViewModel =
            DaggerSettingsComponent.builder()
                .appComponent(appComponent)
                .build()
                .viewModel()
    }
}

@Module
internal class SettingsModule {

    @Provides
    fun viewModel(
        settings: Settings
    ): SettingsViewModel = SettingsViewModel(settings)
}
