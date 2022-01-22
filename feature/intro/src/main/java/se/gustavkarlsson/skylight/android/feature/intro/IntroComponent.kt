package se.gustavkarlsson.skylight.android.feature.intro

import com.squareup.anvil.annotations.MergeComponent
import dagger.Component
import se.gustavkarlsson.skylight.android.core.AppComponent
import se.gustavkarlsson.skylight.android.lib.runversion.RunVersionComponent

@MergeComponent(
    scope = IntroScopeMarker::class,
    dependencies = [
        AppComponent::class,
        RunVersionComponent::class,
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

abstract class IntroScopeMarker private constructor()
