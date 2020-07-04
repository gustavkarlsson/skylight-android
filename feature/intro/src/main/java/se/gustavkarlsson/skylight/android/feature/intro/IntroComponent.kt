package se.gustavkarlsson.skylight.android.feature.intro

import dagger.Component
import se.gustavkarlsson.skylight.android.AppComponent
import se.gustavkarlsson.skylight.android.lib.runversion.RunVersionComponent

@Component(
    dependencies = [
        AppComponent::class,
        RunVersionComponent::class
    ]
)
internal interface IntroComponent {
    fun viewModel(): IntroViewModel

    companion object {
        fun build(): IntroComponent =
            DaggerIntroComponent.builder()
                .appComponent(AppComponent.instance)
                .runVersionComponent(RunVersionComponent.instance)
                .build()
    }
}
