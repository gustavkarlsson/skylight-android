package se.gustavkarlsson.skylight.android.feature.intro

import dagger.Component
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.AppComponent
import se.gustavkarlsson.skylight.android.services.RunVersionManager

@Component(
    modules = [IntroModule::class],
    dependencies = [AppComponent::class]
)
internal interface IntroComponent {
    fun viewModel(): IntroViewModel

    companion object {
        fun build(): IntroComponent =
            DaggerIntroComponent.builder()
                .appComponent(AppComponent.instance)
                .build()
    }
}

@Module
internal class IntroModule {

    @Provides
    fun viewModel(runVersionManager: RunVersionManager): IntroViewModel =
        IntroViewModel(runVersionManager)
}
