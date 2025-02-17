package se.gustavkarlsson.skylight.android.feature.intro

import me.tatarka.inject.annotations.Component
import se.gustavkarlsson.skylight.android.core.ViewModelScope
import se.gustavkarlsson.skylight.android.lib.runversion.RunVersionComponent

@Component
@ViewModelScope
internal abstract class IntroViewModelComponent(
    @Component val runVersionComponent: RunVersionComponent,
) {
    abstract val viewModel: IntroViewModel

    companion object {
        fun build(): IntroViewModelComponent = IntroViewModelComponent::class.create(
            runVersionComponent = RunVersionComponent.instance,
        )
    }
}
