package se.gustavkarlsson.skylight.android.feature.about

import me.tatarka.inject.annotations.Component
import se.gustavkarlsson.skylight.android.core.CoreComponent
import se.gustavkarlsson.skylight.android.core.ViewModelScope
import se.gustavkarlsson.skylight.android.lib.time.TimeComponent

@Component
@ViewModelScope
internal abstract class AboutViewModelComponent(
    @Component val coreComponent: CoreComponent,
    @Component val timeComponent: TimeComponent,
) {
    abstract val viewModel: AboutViewModel

    companion object {
        fun build(): AboutViewModelComponent = AboutViewModelComponent::class.create(
            coreComponent = CoreComponent.instance,
            timeComponent = TimeComponent.instance,
        )
    }
}
