package se.gustavkarlsson.skylight.android.feature.googleplayservices

import me.tatarka.inject.annotations.Component
import se.gustavkarlsson.skylight.android.core.CoreComponent
import se.gustavkarlsson.skylight.android.core.ViewModelScope

@Component
@ViewModelScope
internal abstract class GooglePlayServicesViewModelComponent(
    @Component val coreComponent: CoreComponent,
) {
    abstract val viewModel: GooglePlayServicesViewModel

    companion object {
        fun build(): GooglePlayServicesViewModelComponent = GooglePlayServicesViewModelComponent::class.create(
            coreComponent = CoreComponent.instance,
        )
    }
}
